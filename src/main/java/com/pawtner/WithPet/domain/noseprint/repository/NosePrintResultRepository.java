package com.pawtner.WithPet.domain.noseprint.repository;

import com.pawtner.WithPet.domain.noseprint.entity.NosePrintResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NosePrintResultRepository extends JpaRepository<NosePrintResult, Long> {
    List<NosePrintResult> findBySearch_SearchId(Long searchId);
}
