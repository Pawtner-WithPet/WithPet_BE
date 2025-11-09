package com.pawtner.WithPet.domain.noseprint.controller;

import com.pawtner.WithPet.domain.noseprint.dto.NosePrintResultSaveRequest;
import com.pawtner.WithPet.domain.noseprint.service.NosePrintResultService;
import com.pawtner.WithPet.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/noseprint")
public class NosePrintResultController {

    private final NosePrintResultService service;

    @PostMapping("/result")
    public ResponseEntity<ApiResponse<String>> saveResult(
            @RequestBody NosePrintResultSaveRequest request) {

        ApiResponse<String> response = service.saveResultWithCandidates(request);
        return ResponseEntity.ok(response);
    }
}
