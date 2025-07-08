package com.pawtner.WithPet.domain.noseprint.service;

import com.pawtner.WithPet.domain.member.entity.Member;
import com.pawtner.WithPet.domain.member.repository.MemberRepository;
import com.pawtner.WithPet.domain.noseprint.dto.NosePrintResultSaveRequest;
import com.pawtner.WithPet.domain.noseprint.entity.NosePrint;
import com.pawtner.WithPet.domain.noseprint.entity.NosePrintResult;
import com.pawtner.WithPet.domain.noseprint.entity.NosePrintSearch;
import com.pawtner.WithPet.domain.noseprint.repository.NosePrintRepository;
import com.pawtner.WithPet.domain.noseprint.repository.NosePrintResultRepository;
import com.pawtner.WithPet.domain.noseprint.repository.NosePrintSearchRepository;
import com.pawtner.WithPet.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NosePrintResultService {

    private final NosePrintResultRepository repository;
    private final NosePrintSearchRepository nosePrintSearchRepository;
    private final MemberRepository memberRepository;
    private final NosePrintRepository nosePrintRepository;

    public ApiResponse<String> saveResult(NosePrintResultSaveRequest request) {
        NosePrintSearch search = nosePrintSearchRepository.findById(request.getSearchId())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 search_id: " + request.getSearchId()));

        Member owner = memberRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 owner_id: " + request.getOwnerId()));

        NosePrint nosePrint = nosePrintRepository.findById(request.getNosePrintId())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 nose_print_id: " + request.getNosePrintId()));

        // 4. 결과 엔티티 생성
        NosePrintResult result = NosePrintResult.builder()
                .search(search)
                .owner(owner)
                .nosePrint(nosePrint)
                .matchRate(request.getMatchRate())
                .isMyMissingPet(request.getIsMyMissingPet())
                .build();

        // 5. 저장
        repository.save(result);

        return ApiResponse.success("SUCCESS_SAVE_NOSEPRINT_RESULT", "비문 탐색 결과가 저장되었습니다.", null);
    }
}
