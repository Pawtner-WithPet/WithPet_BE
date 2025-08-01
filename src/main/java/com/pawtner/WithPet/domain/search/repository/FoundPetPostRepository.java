package com.pawtner.WithPet.domain.search.repository;

import com.pawtner.WithPet.domain.search.entity.FoundPetPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FoundPetPostRepository extends JpaRepository<FoundPetPost, Long>, JpaSpecificationExecutor<FoundPetPost> {

}
