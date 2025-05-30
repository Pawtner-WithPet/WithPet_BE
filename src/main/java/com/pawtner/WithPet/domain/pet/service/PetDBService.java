package com.pawtner.WithPet.domain.pet.service;

import com.pawtner.WithPet.domain.pet.dto.PetInfoResponse;
import com.pawtner.WithPet.domain.pet.dto.PetListResponse;
import com.pawtner.WithPet.domain.pet.entity.Pet;
import com.pawtner.WithPet.domain.pet.repository.PetRepository;
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

    private int calculateAge(String birthDt) {
        try {
            LocalDate birth = LocalDate.parse(birthDt);
            return Period.between(birth, LocalDate.now()).getYears();
        } catch (Exception e) {
            return -1;
        }
    }
}