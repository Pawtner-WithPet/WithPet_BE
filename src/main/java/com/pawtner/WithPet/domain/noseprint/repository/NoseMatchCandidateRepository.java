package com.pawtner.WithPet.domain.noseprint.repository;

import com.pawtner.WithPet.domain.noseprint.entity.NoseMatchCandidate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoseMatchCandidateRepository extends JpaRepository<NoseMatchCandidate, Long> {
    List<NoseMatchCandidate> findByResult_ResultId(Long resultId);
}
