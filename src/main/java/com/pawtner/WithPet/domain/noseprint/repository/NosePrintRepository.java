package com.pawtner.WithPet.domain.noseprint.repository;

import com.pawtner.WithPet.domain.noseprint.entity.NosePrint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NosePrintRepository extends JpaRepository<NosePrint, Long> {

    // 특정 반려견 ID로 비문이 이미 존재하는지 여부
    boolean existsByPetId(Long petId);
}
