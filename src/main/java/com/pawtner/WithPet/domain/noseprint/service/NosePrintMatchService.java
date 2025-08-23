package com.pawtner.WithPet.domain.noseprint.service;

import com.pawtner.WithPet.domain.awsS3.service.S3Service;
import com.pawtner.WithPet.domain.noseprint.dto.FlaskCompareResponse;
import com.pawtner.WithPet.domain.noseprint.dto.NoseMatchResultResponse;
import com.pawtner.WithPet.domain.noseprint.dto.NosePrintMatchSaveRequest;
import com.pawtner.WithPet.domain.noseprint.repository.NoseMatchCandidateRepository;
import com.pawtner.WithPet.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NosePrintMatchService {

    private final NoseMatchCandidateRepository repository;

    @Autowired
    private RestTemplate restTemplate;

    private final S3Service s3Service;

    @Value("${flask.server.url:http://localhost:8000}")
    private String flaskServerUrl;

    public ApiResponse<NoseMatchResultResponse> matchResult(MultipartFile nosePrintImg, String status) {
        String imgUrl = null;

        try {
            // 이미지 저장
            imgUrl = s3Service.uploadFile(nosePrintImg, "nose-print-match-test");

            // Flask 서버로 비문 비교 요청
            FlaskCompareResponse flaskResponse = callFlaskCompareApi(imgUrl, status);

            if (flaskResponse == null || flaskResponse.isEmpty()) {
                s3Service.deleteFileByUrl(imgUrl); // 업로드한 파일 삭제
                return ApiResponse.error("FLASK_COMPARE_FAILED", "비문 비교에 실패했습니다.", 500);
            }


            // 가장 높은 일치율 확인
            FlaskCompareResponse.FlaskResult highestMatch = flaskResponse.stream()
                    .max((a, b) -> Double.compare(a.getSimilarity(), b.getSimilarity())) // getsSimilarity() → getSimilarity()
                    .orElse(null);

            if (highestMatch == null) {
                s3Service.deleteFileByUrl(imgUrl); // 업로드한 파일 삭제
                return ApiResponse.error("NO_MATCH_FOUND", "유사한 비문을 찾을 수 없습니다.", 404);
            }

            NoseMatchResultResponse result = new NoseMatchResultResponse();
            result.setNosePrintImg(imgUrl);
            result.setSearchDatetime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
            result.setSearchType(status);
            result.setHighest_score(highestMatch.getSimilarity());
            result.setHightest_nose_print_img(highestMatch.getNose_print_img());
            result.setHightest_post_id(highestMatch.getPost_id());

            return ApiResponse.success("SUCCESS_COMPARE_NOSEPRINT", "비문 비교가 완료되었습니다.", result);

        } catch (Exception e) {
            log.error("비문 비교 처리 중 오류 발생", e);
            if (imgUrl != null) {
                s3Service.deleteFileByUrl(imgUrl);
            }
            return ApiResponse.error("COMPARE_ERROR", "비문 비교 처리 중 오류가 발생했습니다.", 500);
        }
    }

    private FlaskCompareResponse callFlaskCompareApi(String nosePrintImg, String status) {
        try {
            // HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = new HashMap<>();
            body.put("imageUrl", nosePrintImg);
            body.put("status", status);


            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // Flask 서버 호출
            ResponseEntity<FlaskCompareResponse> response = restTemplate.exchange(
                    flaskServerUrl + "/compare_result",
                    HttpMethod.POST,
                    requestEntity,
                    FlaskCompareResponse.class
            );

            return response.getBody();

        } catch (Exception e) {
            log.error("Flask 서버 호출 실패", e);
            return null;
        }
    }
}
