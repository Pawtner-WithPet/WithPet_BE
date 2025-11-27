package com.pawtner.WithPet.domain.walk.repository;

import com.pawtner.WithPet.domain.walk.entity.Walk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalkRepository extends JpaRepository<Walk, Long> {

    List<Walk> findByWalkPets_Pet_IdAndIsSelectedTrueOrderByEndedAtDesc(Long petId);

}
