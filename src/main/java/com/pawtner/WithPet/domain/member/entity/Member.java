package com.pawtner.WithPet.domain.member.entity;

import com.pawtner.WithPet.domain.pet.dto.PetInfoResponse;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String uid;  // 사용자 아이디 (로그인용)

    @Column(nullable = false)
    private String password;

    private String nickname;
    private String name;
    private String birth;
    private String image;   // 프로필 이미지 URL

    @Builder
    public Member(String uid, String password, String nickname, String name, String birth, String image) {
        this.uid = uid;
        this.password = password;
        this.nickname = nickname;
        this.name = name;
        this.birth = birth;
        this.image = image;
    }
}