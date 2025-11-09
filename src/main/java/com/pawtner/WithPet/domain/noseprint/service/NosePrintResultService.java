package com.pawtner.WithPet.domain.noseprint.service;

import com.pawtner.WithPet.domain.member.entity.Member;
import com.pawtner.WithPet.domain.member.repository.MemberRepository;
import com.pawtner.WithPet.domain.noseprint.dto.NosePrintResultSaveRequest;
import com.pawtner.WithPet.domain.noseprint.entity.NosePrint;
import com.pawtner.WithPet.domain.noseprint.entity.NoseMatchCandidate;
import com.pawtner.WithPet.domain.noseprint.entity.NoseMatchResult;
import com.pawtner.WithPet.domain.noseprint.exception.NosePrintNotFoundException;
import com.pawtner.WithPet.domain.noseprint.repository.NosePrintRepository;
import com.pawtner.WithPet.domain.noseprint.repository.NoseMatchCandidateRepository;
import com.pawtner.WithPet.domain.noseprint.repository.NoseMatchResultRepository;
import com.pawtner.WithPet.global.dto.ApiResponse;
import com.pawtner.WithPet.global.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NosePrintResultService {

    private final NoseMatchCandidateRepository repository;
    private final NoseMatchResultRepository noseMatchResultRepository;
    private final MemberRepository memberRepository;
    private final NosePrintRepository nosePrintRepository;

    @Transactional
    public ApiResponse<String> saveResultWithCandidates(NosePrintResultSaveRequest req) {
        // 1) NoseMatchResult 생성
        Member owner = memberRepository.findById(req.getOwnerId())
                .orElseThrow(() -> new MemberNotFoundException("유효하지 않은 owner_id: " + req.getOwnerId()));

        NoseMatchResult result = NoseMatchResult.builder()
                .owner(owner)
                .nosePrintImg(req.getNosePrintImg())
                .searchLocation(req.getSearchLocation())
                .searchDatetime(req.getSearchDatetime())
                .searchType(req.getSearchType())
                .highestScore(req.getHighestScore())
                .highestNosePrintImg(req.getHighestNosePrintImg())
                .highestPostId(req.getHighestPostId())
                .build();

        // 2) 후보 목록 매핑 & 추가
        for (NosePrintResultSaveRequest.CandidateSave c : req.getCandidates()) {
            Member candOwner = memberRepository.findById(c.getOwnerId())
                    .orElseThrow(() -> new MemberNotFoundException("유효하지 않은 owner_id: " + c.getOwnerId()));
            NosePrint nosePrint = nosePrintRepository.findById(c.getNosePrintId())
                    .orElseThrow(() -> new NosePrintNotFoundException("유효하지 않은 nose_print_id: " + c.getNosePrintId()));


            NoseMatchCandidate candidate = NoseMatchCandidate.builder()
                    //.result(result)
                    .owner(candOwner)
                    .nosePrint(nosePrint)
                    .nosePrintImg(c.getNosePrintImg())
                    .matchRate(c.getMatchRate())
                    .postId(c.getPostId())
                    .build();

            result.addCandidate(candidate);
        }

        // 3) 저장
        NoseMatchResult saved = noseMatchResultRepository.save(result);

        return ApiResponse.success("SUCCESS_SAVE_NOSEPRINT_RESULT", "비문 탐색 결과가 저장되었습니다.", null);
//        return saved.getResultId();
    }

//    public ApiResponse<String> saveResult(NosePrintResultSaveRequest request) {
//        NoseMatchResult resultId = noseMatchResultRepository.findById(request.getResultId())
//                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 result_id: " + request.getResultId()));
//
//        Member owner = memberRepository.findById(request.getOwnerId())
//                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 owner_id: " + request.getOwnerId()));
//
//        NosePrint nosePrint = nosePrintRepository.findById(request.getNosePrintId())
//                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 nose_print_id: " + request.getNosePrintId()));
//
//        // 4. 결과 엔티티 생성
//        NoseMatchCandidate result = NoseMatchCandidate.builder()
//                .result(resultId)
//                .owner(owner)
//                .nosePrint(nosePrint)
//                .matchRate(request.getMatchRate())
//                .build();
//
//        // 5. 저장
//        repository.save(result);
//
//        return ApiResponse.success("SUCCESS_SAVE_NOSEPRINT_RESULT", "비문 탐색 결과가 저장되었습니다.", null);
//    }
}
