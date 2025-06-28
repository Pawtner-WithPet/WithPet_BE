package com.pawtner.WithPet.domain.pet.service;

import com.pawtner.WithPet.domain.awsS3.exception.InvalidImageException;
import com.pawtner.WithPet.domain.awsS3.service.S3Service;
import com.pawtner.WithPet.domain.pet.dto.PetDetailResponse;
import com.pawtner.WithPet.domain.pet.dto.PetInfoResponse;
import com.pawtner.WithPet.domain.pet.dto.PetListResponse;
import com.pawtner.WithPet.domain.pet.entity.Pet;
import com.pawtner.WithPet.domain.pet.repository.PetRepository;
import com.pawtner.WithPet.global.exception.PetInfoNotFoundException;
import com.pawtner.WithPet.global.exception.PetNotFoundException;
import com.pawtner.WithPet.global.exception.UnauthorizedPetAccessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PetDBService {

    private final PetRepository petRepository;
    private final S3Service s3Service;

    public Pet savePetInfo(PetInfoResponse.Body.Item item, Long userId) {
        Pet pet = Pet.builder()
                .dogRegNo(item.getDogRegNo())
                .rfidCd(item.getRfidCd())
                .dogNm(item.getDogNm())
                .birthDt(item.getBirthDt())
                .sexNm(item.getSexNm())
                .kindNm(item.getKindNm())
                .neuterYn(item.getNeuterYn())
                .orgNm(item.getOrgNm())
                .aprGbNm(item.getAprGbNm())
                .officeTel(item.getOfficeTel())
                .rfidGubun(item.getRfidGubun())
                .userId(userId)
                .build();

        return petRepository.save(pet);
    }

    public List<PetListResponse> getPetsByUser(Long userId) {
        return petRepository.findByUserId(userId).stream()
                .map(this::toPetListResponse)
                .collect(Collectors.toList());
    }

    public PetListResponse toPetListResponse(Pet pet) {
        return PetListResponse.builder()
                .id(pet.getId())
                .dogRegNo(pet.getDogRegNo())
                .dogNm(pet.getDogNm())
                .kindNm(pet.getKindNm())
                .sexNm(pet.getSexNm())
                .dogAge(calculateAge(pet.getBirthDt()))
                .dogImg(pet.getDogImg())
                .build();
    }

    // 반려견 상세 정보 조회
    public PetDetailResponse getPetDetailInfo(Long petId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new IllegalArgumentException("해당 반려견 정보를 찾을 수 없습니다."));

        return PetDetailResponse.builder()
                .dogRegNo(pet.getDogRegNo())
                .rfidCd(pet.getRfidCd())
                .dogNm(pet.getDogNm())
                .dogAge(calculateAge(pet.getBirthDt()))
                .sexNm(pet.getSexNm())
                .kindNm(pet.getKindNm())
                .neuterYn(pet.getNeuterYn())
                .orgNm(pet.getOrgNm())
                .aprGbNm(pet.getAprGbNm())
                .officeTel(pet.getOfficeTel())
                .rfidGubun(pet.getRfidGubun())
                .userId(pet.getUserId())
                .features(pet.getFeatures())
                .nosePrint(pet.getNosePrint())
                .dogImg(pet.getDogImg())
                .build();
    }

    // 반려견 정보 수정
    public Pet updatePetInfo(Long userId, Long petId, String features) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new IllegalArgumentException("해당 반려견 정보를 찾을 수 없습니다."));

        if (userId == null || !userId.equals(pet.getUserId())) {
            throw new SecurityException("수정 권한이 없습니다.");
        }

        if (features != null && !features.isBlank()) {
            pet.updatePetInfo(features);
        }

        return petRepository.save(pet);
    }

    // 반려견 이미지 수정
    public Pet updatePetImage(Long userId, Long petId, MultipartFile dogImg) {
        // 유효성 검사
        validateImageFile(dogImg);

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new PetNotFoundException("해당 반려견 정보를 찾을 수 없습니다."));

        if (!userId.equals(pet.getUserId())) {
            throw new UnauthorizedPetAccessException("수정 권한이 없습니다.");
        }

        // 기존 이미지가 있는 경우 → 삭제 (S3 불필요한 스토리지 비용 증가 방지를 위해 정리)
        if (pet.getDogImg() != null) {
            s3Service.deleteFileByUrl(pet.getDogImg());
        }

        // 새 이미지 업로드
        String dogImgUrl = s3Service.uploadFile(dogImg, "dog-images");
        pet.updateDogImg(dogImgUrl);

        return petRepository.save(pet);
    }
    // 반려견 이미지 수정 - 유효성 검증
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


    private int calculateAge(String birthDt) {
        try {
            LocalDate birth = LocalDate.parse(birthDt);
            return Period.between(birth, LocalDate.now()).getYears();
        } catch (Exception e) {
            return -1;
        }
    }

    public Pet getPetById(Long petId) {
        return petRepository.findById(petId)
                .orElseThrow(() -> new PetInfoNotFoundException("해당 반려견 정보를 찾을 수 없습니다."));
    }

    // 반려견 정보 갱신
    @Transactional
    public Pet renewalPetInfo(PetInfoResponse.Body.Item item, Long petId) {
        Pet existingPet = petRepository.findById(petId)
                .orElseThrow(() -> new PetInfoNotFoundException("해당 반려견 정보를 찾을 수 없습니다."));

        existingPet.renewalFromAPI(item);
        return existingPet;
    }
}