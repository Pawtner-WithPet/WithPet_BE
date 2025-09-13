package com.pawtner.WithPet.domain.search.realtime.repository;

import com.pawtner.WithPet.domain.search.realtime.entity.RealTimeSearch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RealTimeSearchRepository extends JpaRepository<RealTimeSearch, Long> {
    List<RealTimeSearch> findByActiveTrue();
    List<RealTimeSearch> findByUserId(Long userId);
    Optional<RealTimeSearch> findTopByUserIdAndPetIdOrderByUpdatedAtDesc(Long userId, Long petId);

    Optional<RealTimeSearch> findTopByUserIdAndActiveTrueAndLastRunAtIsNotNullOrderByLastRunAtDesc(Long userId);
    Optional<RealTimeSearch> findTopByUserIdAndLastRunAtIsNotNullOrderByLastRunAtDesc(Long userId);

}