package com.pawtner.WithPet.domain.search.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawtner.WithPet.domain.search.dto.FoundPetPostResponse;
import com.pawtner.WithPet.domain.search.dto.ShelterApiResponse;
import com.pawtner.WithPet.domain.search.dto.ShelterSearchResponse;
import com.pawtner.WithPet.domain.search.entity.FoundPetPost;
import com.pawtner.WithPet.domain.search.entity.LostPetPost;
import com.pawtner.WithPet.domain.search.exception.InvalidSearchKeywordException;
import com.pawtner.WithPet.domain.search.exception.LostPetPostNotFoundException;
import com.pawtner.WithPet.domain.search.exception.ShelterApiException;
import com.pawtner.WithPet.domain.search.repository.FoundPetPostRepository;
import com.pawtner.WithPet.domain.search.repository.LostPetPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.pawtner.WithPet.domain.search.repository.FoundPetPostSpecs.containsKeywords;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

    @Value("${openapi.shelterAPIKey}")
    private String shelterAPIKey;

    @Value("${openapi.shelterAPIUrl}")
    private String shelterApiUrl;

    private final FoundPetPostRepository foundPetPostRepository;
    private final LostPetPostRepository lostPetPostRepository;

    // [ 발견동물 기반 탐색 ]
    public List<FoundPetPostResponse> searchFoundPets(Long petId, List<String> keywords) {

        log.info("[SVC] START searchFoundPets petId={}, keywords={}", petId, keywords); // 로그 2


        // 키워드 유효성 검사
        if (keywords == null || keywords.stream().allMatch(k -> k == null || k.trim().isEmpty())) {
            throw new InvalidSearchKeywordException("키워드를 1개 이상 입력해야 합니다.");
        }
        log.info("[SVC] keywords validation OK"); // 로그 3

        // 실종글 조회 (petId → lostDate)
        LostPetPost lost = lostPetPostRepository.findTopByPetIdOrderByLostDateDesc(petId)
                .orElseThrow(() -> new LostPetPostNotFoundException("해당 petId 로 등록된 실종 글이 없습니다. petId=" + petId));

        LocalDateTime lostDate = lost.getLostDate();
        log.info("[SVC] lostDate={}", lost.getLostDate()); // 로그 4

        // Specification 생성
        Specification<FoundPetPost> spec = containsKeywords(keywords, lostDate);

        // 발견글 조회 실행
        List<FoundPetPost> entities = foundPetPostRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "foundDate"));

        // 디버그용 매칭 키워드 로그
        for (FoundPetPost fp : entities) {
            List<String> hit = keywords.stream()
                    .filter(k -> !k.isBlank())
                    .filter(k -> isKeywordMatched(fp, k))
                    .toList();
            log.info("[MATCH] postId={} -> {}", fp.getFoundPostId(), hit); // 로그 5
        }

        // DTO 매핑 후 반환
        return entities.stream()
                .map(FoundPetPostResponse::from)
                .toList();

    }
    // 개발 로그용)특정 발견글이 키워드를 포함하는지 여부
    private boolean isKeywordMatched(FoundPetPost fp, String keyword) {
        String p = keyword.toLowerCase();
        return Optional.ofNullable(fp.getKindNm()).map(String::toLowerCase).orElse("").contains(p)
                || Optional.ofNullable(fp.getFoundLocation()).map(String::toLowerCase).orElse("").contains(p)
                || Optional.ofNullable(fp.getDescription()).map(String::toLowerCase).orElse("").contains(p);
    }




    // [ 보호소 기반 탐색 ]
    public List<ShelterSearchResponse> getShelterSearch(Long pet_id, List<String> keywordList){
        // 실종글 정보 가져오기
        LostPetPost post = lostPetPostRepository.findBypetId(pet_id)
                .orElseThrow(() -> new LostPetPostNotFoundException("해당 실종 글이 존재하지 않습니다.")); // 예외 수정 필요

        // 실종 날짜 형식 변경
        String lostDate = post.getLostDate().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // 보호소 조회 url 설정 (조회 시작일 -> 실종글 등록일 / 종 -> 개로 고정)
        String url = shelterApiUrl +
                "?serviceKey=" + shelterAPIKey +  // URL 인코딩된 키 그대로
                "&bgnde=" + lostDate +
                "&upkind=417000" +
                "&numOfRows=100" +
                "&pageNo=1" +
                "&_type=json";


        try {
            URI uri = new URI(url);

            // 보호소 api 조회
            RestClient restClient = RestClient.create();
            String response = restClient.get()
                    .uri(uri)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, errorResponse) -> {
                        throw new ShelterApiException("보호소 API 호출 실패. 상태코드: " + errorResponse.getStatusCode());
                    })
                    .body(String.class);

//            log.info("보호소 API 요청 URL: {}", url);
//            log.info("보호소 API 응답 내용: {}", response);

            if (response.trim().startsWith("<")) {
                log.error("XML 응답 - 인증키 또는 파라미터 오류: {}", response);
                throw new ShelterApiException("보호소 API 인증 실패");
            }

            // 객체로 변환
            List<ShelterApiResponse> shelterList = parseApiResponse(response);

            // 키워드 필터링
            if (!keywordList.isEmpty()) {
                shelterList = filterByKeywords(shelterList, keywordList);
            }

            return shelterList.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());

        } catch (ShelterApiException e) {
            throw e;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            log.error("보호소 검색 중 예상치 못한 오류 발생. URL: {}", url, e);
            throw new ShelterApiException("보호소 데이터 조회 중 오류가 발생했습니다.");
        }
    }

    // 보호소 정보 키워드
    private List<ShelterApiResponse> filterByKeywords(List<ShelterApiResponse> shelterList, List<String> keywordList) {

        return shelterList.stream()
                .filter(shelter -> {
                    String combined = Stream.of(
                                    shelter.getSpecialMark(),
                                    shelter.getKindNm(),
                                    shelter.getHappenPlace(),
                                    shelter.getColorCd(),
                                    shelter.getSexCd(),
                                    shelter.getNeuterYn()
                            )
                            .filter(Objects::nonNull)
                            .filter(s -> !s.isBlank()) // 빈 문자열도 제외
                            .collect(Collectors.joining(" "))
                            .toLowerCase();

                    return keywordList.stream()
                            .map(String::toLowerCase)
                            .anyMatch(combined::contains);
                })
                .collect(Collectors.toList());
    }

    // 결과 변환
    private ShelterSearchResponse convertToResponse(ShelterApiResponse dto) {
        return ShelterSearchResponse.builder()
                .petImg(dto.getPopfile())
                .foundLocation(dto.getHappenPlace())
                .feature(dto.getSpecialMark())
                .shelterName(dto.getCareNm())
                .shelterLocation(dto.getCareAddr())
                .shelterTel(dto.getCareTel())
                .build();
    }

    private List<ShelterApiResponse> parseApiResponse(String json) {
        List<ShelterApiResponse> result = new ArrayList<>();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            JsonNode root = objectMapper.readTree(json);

            // API 응답 상태 확인
            JsonNode itemsNode = root.path("response")
                    .path("body")
                    .path("items")
                    .path("item");

            if (itemsNode.isArray()) {
                for (JsonNode itemNode : itemsNode) {
                    ShelterApiResponse response = objectMapper.treeToValue(itemNode, ShelterApiResponse.class);
                    result.add(response);
                }
            } else if (itemsNode.isObject()) {
                ShelterApiResponse response = objectMapper.treeToValue(itemsNode, ShelterApiResponse.class);
                result.add(response);
            }

        } catch (Exception e) {
            log.error("보호소 API 응답 파싱 실패", e);
            throw new RuntimeException("보호소 데이터 파싱 실패", e);
        }

        return result;
    }
}
