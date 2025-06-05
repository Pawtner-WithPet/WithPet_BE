package com.pawtner.WithPet.domain.pet.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawtner.WithPet.domain.member.entity.Member;
import com.pawtner.WithPet.domain.pet.dto.PetInfoResponse;
import com.pawtner.WithPet.domain.pet.entity.Pet;
import com.pawtner.WithPet.domain.member.service.MemberService;
import com.pawtner.WithPet.global.exception.PetApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class PetService {

    @Value("${openapi.petAPIKey}")
    private String PET_API_KEY;

    @Value("${openapi.petAPIUrl}")
    private String PET_API_URL;

    private final PetDBService petDBService;
    private final MemberService memberService;

    // private final ObjectMapper objectMapper; 전역 objectMapper

    // [ 국가동물보호정보시스템 등록 동물 openAPI 호출 -> PetInfoResponse 객체 반환 ]
    public PetInfoResponse getPetInfo(String dogRegNo, String rfidCd, String ownerNm, String ownerBirth) {
        try {
            StringBuilder urlBuilder = new StringBuilder(PET_API_URL);
            urlBuilder.append("?serviceKey=").append(PET_API_KEY);
            urlBuilder.append("&dog_reg_no=").append(URLEncoder.encode(dogRegNo, StandardCharsets.UTF_8));
            urlBuilder.append("&rfid_cd=").append(URLEncoder.encode(rfidCd, StandardCharsets.UTF_8));
            urlBuilder.append("&owner_nm=").append(URLEncoder.encode(ownerNm, StandardCharsets.UTF_8));
            urlBuilder.append("&owner_birth=").append(URLEncoder.encode(ownerBirth, StandardCharsets.UTF_8));
            urlBuilder.append("&_type=json");

            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            try (BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }

                log.info("API 응답 원문:\n{}", sb.toString());

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.enable(DeserializationFeature.UNWRAP_ROOT_VALUE);
                return objectMapper.readValue(sb.toString(), PetInfoResponse.class);
            } finally {
                conn.disconnect();
            }

        } catch (IOException | RuntimeException e) {
            throw new PetApiException("공공 API 호출 또는 응답 처리 중 오류 발생");
        }
    }


    // [ 반려견 정보 갱신 ]
    @Transactional
    public Pet renewalPetInfo(Long petId) {
        // 1. 반려견 조회
        Pet pet = petDBService.getPetById(petId);

        // 2. 사용자 정보 조회
        Member member = memberService.getUserById(pet.getUserId());

        // 3. 공공 API 호출
        PetInfoResponse response = getPetInfo(
                pet.getDogRegNo(),
                pet.getRfidCd(),
                member.getName(),
                member.getBirth()
        );

        // 4. DB 정보 갱신
        PetInfoResponse.Body.Item item = response.getBody().getItem();
        return petDBService.renewalPetInfo(item, petId);
    }
}