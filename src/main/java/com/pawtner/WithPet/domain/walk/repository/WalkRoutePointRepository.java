package com.pawtner.WithPet.domain.walk.repository;

import com.pawtner.WithPet.domain.walk.entity.WalkRoutePoint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalkRoutePointRepository extends JpaRepository<WalkRoutePoint, Long> {

    List<WalkRoutePoint> findByWalk_WalkIdOrderBySeqAsc(Long walkId);
}
