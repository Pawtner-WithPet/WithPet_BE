package com.pawtner.WithPet.domain.member.repository;

import com.pawtner.WithPet.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUid(String uid);
}
