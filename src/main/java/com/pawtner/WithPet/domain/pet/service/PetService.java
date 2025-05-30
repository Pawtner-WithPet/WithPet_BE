package com.pawtner.WithPet.domain.pet.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawtner.WithPet.domain.pet.dto.PetInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

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

    // private final ObjectMapper objectMapper; 전역 objectMapper

    // [ 국가동물보호정보시스템 등록 동물 openAPI 호출 -> PetInfoResponse 객체 반환 ]
    public PetInfoResponse getPetInfo(String dogRegNo, String rfidCd, String ownerNm, String ownerBirth) throws IOException {
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

            // 지역으로 변경 ← 전역 설정은 등록 시 JSON으로 받아올 수가 없음
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(DeserializationFeature.UNWRAP_ROOT_VALUE); // JSON 루트 언래핑 설정

            PetInfoResponse response = objectMapper.readValue(sb.toString(), PetInfoResponse.class);
            log.info("파싱된 응답 DTO: {}", response);

            return response;
        } finally {
            conn.disconnect();
        }
    }
}