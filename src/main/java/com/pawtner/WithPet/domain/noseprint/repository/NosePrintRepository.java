package com.pawtner.WithPet.domain.noseprint.repository;

import com.pawtner.WithPet.domain.noseprint.entity.NosePrint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NosePrintRepository extends JpaRepository<NosePrint, Long> {

    // 특정 반려견 ID로 비문이 이미 존재하는지 여부
    boolean existsByPetId(Long petId);

    // 특정 사용자 기준, 비문이 등록된 반려견 목록 조회
    @Query("SELECT np.pet.id FROM NosePrint np WHERE np.owner.id = :ownerId AND np.pet IS NOT NULL AND np.nosePrintImg IS NOT NULL")
    List<Long> findPetIdsWithNosePrintByOwnerId(@Param("ownerId") Long ownerId);

    // petId로 특정 반려견 비문 이미지 조회
    Optional<NosePrint> findByPetId(Long petId);
}
