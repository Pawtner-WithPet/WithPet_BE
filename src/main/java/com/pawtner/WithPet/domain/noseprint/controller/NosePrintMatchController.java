package com.pawtner.WithPet.domain.noseprint.controller;

import com.pawtner.WithPet.domain.noseprint.dto.FlaskCompareResponse;
import com.pawtner.WithPet.domain.noseprint.dto.NoseMatchResultResponse;
import com.pawtner.WithPet.domain.noseprint.dto.NosePrintMatchSaveRequest;
import com.pawtner.WithPet.domain.noseprint.service.NosePrintMatchService;
import com.pawtner.WithPet.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/noseprint")
public class NosePrintMatchController {

    private final NosePrintMatchService service;

    @PostMapping("/match")
    public ResponseEntity<ApiResponse<NoseMatchResultResponse>> matchResult(
            @RequestParam MultipartFile nosePrintImg,
            @RequestParam String status) {

        ApiResponse<NoseMatchResultResponse> response = service.matchResult(nosePrintImg, status);

        return ResponseEntity.ok(response);
    }
}
