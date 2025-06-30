package com.pawtner.WithPet.domain.noseprint.service;

import com.pawtner.WithPet.domain.noseprint.dto.NosePrintResultSaveRequest;
import com.pawtner.WithPet.domain.noseprint.entity.NosePrintResult;
import com.pawtner.WithPet.domain.noseprint.repository.NosePrintResultRepository;
import com.pawtner.WithPet.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NosePrintResultService {

    private final NosePrintResultRepository repository;

    public ApiResponse<String> saveResult(NosePrintResultSaveRequest request) {
        NosePrintResult result = NosePrintResult.builder()
                .searchId(request.getSearchId())
                .ownerId(request.getOwnerId())
                .nosePrintId(request.getNosePrintId())
                .matchRate(request.getMatchRate())
                .isMyMissingPet(request.getIsMyMissingPet())
                .build();

        repository.save(result);
        return ApiResponse.success("SUCCESS_SAVE_NOSEPRINT_RESULT", "비문 탐색 결과가 저장되었습니다.", null);
    }
}
