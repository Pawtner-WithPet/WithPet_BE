package com.pawtner.WithPet.domain.pet.repository;

import com.pawtner.WithPet.domain.pet.entity.Pet;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PetRepository extends JpaRepository<Pet, Long> {
    List<Pet> findByUserId(Long userId);

    // DogRegNo에 따른 반려견 정보 출력
    Optional<Pet> findByDogRegNo(String dogRegNo);

    // 사용자별 실종 반려견 목록 조회
    List<Pet> findByUserIdAndStatusFalse(Long userId);

    // 실종 상태 변경
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Pet p SET p.status = :status WHERE p.id = :petId")
    int updateStatusById(@Param("petId") Long petId, @Param("status") Boolean status);

}
