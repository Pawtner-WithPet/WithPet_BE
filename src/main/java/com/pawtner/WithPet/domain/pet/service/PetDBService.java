package com.pawtner.WithPet.domain.pet.service;

import com.pawtner.WithPet.domain.pet.dto.PetDetailResponse;
import com.pawtner.WithPet.domain.pet.dto.PetInfoResponse;
import com.pawtner.WithPet.domain.pet.dto.PetListResponse;
import com.pawtner.WithPet.domain.pet.entity.Pet;
import com.pawtner.WithPet.domain.pet.repository.PetRepository;
import com.pawtner.WithPet.global.exception.PetInfoNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PetDBService {

    private final PetRepository petRepository;

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
                .dogRegNo(pet.getDogRegNo())
                .dogNm(pet.getDogNm())
                .kindNm(pet.getKindNm())
                .sexNm(pet.getSexNm())
                .dogAge(calculateAge(pet.getBirthDt()))
                .dogImg(pet.getDogImg())
                .build();
    }

    // 반려견 상세 정보 조회
    public PetDetailResponse getPetDetailInfo(String dogRegNo) {
        Pet pet = petRepository.findByDogRegNo(dogRegNo)
                .orElseThrow(() -> new PetInfoNotFoundException("해당 반려견 정보를 찾을 수 없습니다."));

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

    private int calculateAge(String birthDt) {
        try {
            LocalDate birth = LocalDate.parse(birthDt);
            return Period.between(birth, LocalDate.now()).getYears();
        } catch (Exception e) {
            return -1;
        }
    }
}