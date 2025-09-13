package com.pawtner.WithPet.domain.search.realtime.repository;

import com.pawtner.WithPet.domain.search.realtime.entity.SnsPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SnsPostRepository extends JpaRepository<SnsPost, Long> {
    List<SnsPost> findByPostedAtAfterOrderByPostedAtAsc(LocalDateTime after);
}
