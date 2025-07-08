package com.pawtner.WithPet.domain.noseprint.service;

import com.pawtner.WithPet.domain.awsS3.exception.InvalidImageException;
import com.pawtner.WithPet.domain.awsS3.service.S3Service;
import com.pawtner.WithPet.domain.noseprint.dto.*;
import com.pawtner.WithPet.domain.member.entity.Member;
import com.pawtner.WithPet.domain.member.repository.MemberRepository;
import com.pawtner.WithPet.domain.noseprint.entity.NosePrint;
import com.pawtner.WithPet.domain.noseprint.entity.NosePrintResult;
import com.pawtner.WithPet.domain.noseprint.entity.NosePrintSearch;
import com.pawtner.WithPet.domain.noseprint.exception.DuplicateNosePrintException;
import com.pawtner.WithPet.domain.noseprint.exception.NosePrintNotFoundException;
import com.pawtner.WithPet.domain.noseprint.exception.SearchNotFoundException;
import com.pawtner.WithPet.domain.noseprint.repository.NosePrintRepository;
import com.pawtner.WithPet.domain.noseprint.repository.NosePrintResultRepository;
import com.pawtner.WithPet.domain.noseprint.repository.NosePrintSearchRepository;
import com.pawtner.WithPet.domain.pet.dto.PetListResponse;
import com.pawtner.WithPet.domain.pet.entity.Pet;
import com.pawtner.WithPet.domain.pet.repository.PetRepository;
import com.pawtner.WithPet.global.dto.ApiResponse;
import com.pawtner.WithPet.global.exception.MemberNotFoundException;
import com.pawtner.WithPet.global.exception.PetNotFoundException;
import com.pawtner.WithPet.global.exception.UnauthorizedPetAccessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NosePrintService {

    private final NosePrintRepository nosePrintRepository;
    private final PetRepository petRepository;
    private final MemberRepository memberRepository;
    private final S3Service s3Service;
    private final NosePrintSearchRepository nosePrintSearchRepository;
    private final NosePrintResultRepository nosePrintResultRepository;


    // [ 비문 이미지 등록 ]
    public NosePrint saveNosePrint(MultipartFile nosePrintImg, Long petId, Long ownerId) {

        // 유효성 검사
        validateImageFile(nosePrintImg);

        // 연관 엔티티 조회 - Pet
        Pet pet = null;
        if (petId != null) {
            pet = petRepository.findById(petId)
                    .orElseThrow(() -> new PetNotFoundException("해당 반려견 정보를 찾을 수 없습니다. 반려견 ID: " + petId));
        }

        // 중복 확인 (1개체당 1비문)
        if (pet != null && nosePrintRepository.existsByPetId(petId)) {
            throw new DuplicateNosePrintException("이 반려견은 이미 비문이 등록돼 있습니다.");
        }

        // 연관 엔티티 조회 - Member
        Member owner = null;
        if (ownerId != null) {
            owner = memberRepository.findById(ownerId)
                    .orElseThrow(() -> new MemberNotFoundException("해당 사용자 정보를 찾을 수 없습니다. 사용자 ID: " + ownerId));
        }

        // 권한 확인
        if (pet != null && owner != null && !Objects.equals(ownerId, pet.getUserId())) {
            throw new UnauthorizedPetAccessException("등록 권한이 없습니다");
        }


        // 비문 이미지 업로드 (aws s3)
        String nosePrintUrl = s3Service.uploadFile(nosePrintImg, "nose-print-images");

        // 엔티티 생성 & 저장
        NosePrint nosePrint = nosePrintRepository.save(
                NosePrint.builder()
                        .nosePrintImg(nosePrintUrl)
                        .pet(pet)
                        .owner(owner)
                        .build()
        );

        return nosePrint;
    }

    // 비문 이미지 등록 - 유효성 검증
    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidImageException("이미지 파일이 비어 있습니다.");
        }

        String contentType = file.getContentType();
        if (contentType == null ||
                (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
            throw new InvalidImageException("지원하지 않는 이미지 형식입니다. (JPEG, PNG만 가능)");
        }
    }


    // [ 비문 상세 정보 조회 ]
    public NosePrintDetailResponse getNosePrintDetailInfo(Long nosePrintId) {
        NosePrint nosePrint = nosePrintRepository.findById(nosePrintId)
                .orElseThrow(() -> new IllegalArgumentException("해당 비문 정보를 찾을 수 없습니다. 비문 ID: " + nosePrintId));

        return NosePrintDetailResponse.builder()
                .nosePrintImg(nosePrint.getNosePrintImg())
                .petId(nosePrint.getPet() != null ? nosePrint.getPet().getId() : null)
                .ownerId(nosePrint.getOwner() != null ? nosePrint.getOwner().getId() : null)
                .registerDatetime(nosePrint.getRegisterDatetime())
                .build();
    }


    // [ 비문 등록 반려견 조회 ]
    public ApiResponse<List<PetListResponse>> getPetsWithNosePrint(Long userId) {
        if (!memberRepository.existsById(userId)) {
            throw new MemberNotFoundException("해당 사용자 정보를 찾을 수 없습니다. 사용자 ID: " + userId);
        }

        List<Long> PetIds = nosePrintRepository.findPetIdsWithNosePrintByOwnerId(userId);
        List<Pet> pets = petRepository.findAllById(PetIds);

        List<PetListResponse> result = pets.stream()
                .map(PetListResponse::fromEntity)
                .toList();

        return ApiResponse.success("SUCCESS_GET_NOSEPRINT_DOGS", "비문 등록된 반려견 목록 조회 성공", result);
    }


    // [ 특정 반려견 비문 이미지 조회 ]
    public ApiResponse<NosePrintImageResponse> getNosePrintByPetId(Long petId, Long userId) {
        // 반려견 존재 확인
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new PetNotFoundException("해당 반려견 정보를 찾을 수 없습니다. 반려견 ID: " + petId));

        // 사용자 존재 확인
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new MemberNotFoundException("해당 사용자 정보를 찾을 수 없습니다. 사용자 ID: " + userId));

        // 권한 확인 (본인 반려견인지 체크)
        if (!pet.getUserId().equals(member.getId())) {
            throw new UnauthorizedPetAccessException("비문 조회 권한이 없습니다.");
        }

        // 비문 조회
        NosePrint nosePrint = nosePrintRepository.findByPetId(petId)
                .orElseThrow(() -> new NosePrintNotFoundException("해당 반려견의 비문이 등록되어 있지 않습니다."));

        NosePrintImageResponse response = NosePrintImageResponse.builder()
                .nosePrintId(nosePrint.getNose_print_id())
                .nosePrintImg(nosePrint.getNosePrintImg())
                .petId(petId)
                .ownerId(member.getId())
                .registerDatetime(nosePrint.getRegisterDatetime())
                .build();

        return ApiResponse.success("SUCCESS_GET_NOSEPRINT", "비문 이미지 조회 성공", response);
    }

    // [비문 결과 목록 조회]
    public List<NosePrintListResponse> getNosePrintList(Long ownerId) {
        if (!memberRepository.existsById(ownerId)) {
            throw new MemberNotFoundException("해당 사용자 정보를 찾을 수 없습니다.");
        }

        return nosePrintSearchRepository.findByOwnerIdOrderBySearchDatetimeDesc(ownerId).stream()
                .map(this::toNosePrintListResponse)
                .collect(Collectors.toList());
    }

    public NosePrintListResponse toNosePrintListResponse(NosePrintSearch nosePrint){
        return NosePrintListResponse.builder()
                .searchId(nosePrint.getSearchId())
                .ownerId(nosePrint.getOwner().getId())
                .nosePrintImg(nosePrint.getNosePrintImg())
                .searchLocation(nosePrint.getSearchLocation())
                .searchDatetime(nosePrint.getSearchDatetime())
                .searchType(nosePrint.getSearchType())
                .highestScore(nosePrint.getHighestScore())
                .build();
    }

    // [비문 결과 상세 조회]
    public NosePrintListDetailResponse getNosePrintListDetail(Long searchId){
        NosePrintSearch search = nosePrintSearchRepository.findBySearchId(searchId)
                .orElseThrow(() -> new SearchNotFoundException("해당 비문 탐색 결과를 찾을 수 없습니다."));

        List<NosePrintResult> results = nosePrintResultRepository.findBySearch_SearchId(searchId);

        List<NosePrintResultResponse> resultList = results.stream().map(result -> {

            NosePrint nosePrint = result.getNosePrint();
            Long petId = nosePrint != null && nosePrint.getPet() != null ? nosePrint.getPet().getId() : null;

            return new NosePrintResultResponse(
                    petId,
                    result.getResultId(),
                    result.getOwner().getId(),
                    nosePrint != null ? nosePrint.getNose_print_id() : null,
                    nosePrint != null ? nosePrint.getNosePrintImg() : null,
                    result.getMatchRate(),
                    result.getIsMyMissingPet()
            );
        }).collect(Collectors.toList());

        return NosePrintListDetailResponse.builder()
                .searchId(search.getSearchId())
                .ownerId(search.getOwner().getId())
                .nosePrintImg(search.getNosePrintImg())
                .searchLocation(search.getSearchLocation())
                .searchDatetime(search.getSearchDatetime())
                .searchType(search.getSearchType())
                .result(resultList)
                .build();
    }
}
