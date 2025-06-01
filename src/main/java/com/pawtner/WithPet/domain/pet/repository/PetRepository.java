package com.pawtner.WithPet.domain.pet.repository;

import com.pawtner.WithPet.domain.pet.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PetRepository extends JpaRepository<Pet, Long> {
    List<Pet> findByUserId(Long userId);

    // DogRegNo에 따른 반려견 정보 출력
    Optional<Pet> findByDogRegNo(String dogRegNo);

}
