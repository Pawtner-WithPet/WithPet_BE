package com.pawtner.WithPet.domain.search.repository;

import com.pawtner.WithPet.domain.search.entity.LostPetPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LostPetPostRepository extends JpaRepository<LostPetPost, Long>, JpaSpecificationExecutor<LostPetPost> {

    // 특정 반려견 ID로 실종 글 중 lostDate가 가장 최신인 글 한 건
    Optional<LostPetPost> findTopByPetIdOrderByLostDateDesc(Long petId);

    Optional<LostPetPost> findBypetId(Long petId);
}
