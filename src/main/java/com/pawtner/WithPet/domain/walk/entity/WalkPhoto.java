package com.pawtner.WithPet.domain.walk.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "walk_photo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalkPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "photo_id")
    private Long photoId;

    @Column(name = "lat", precision = 9, scale = 6)
    private BigDecimal lat;

    @Column(name = "lng", precision = 9, scale = 6)
    private BigDecimal lng;

    @Column(name = "taken_at")
    private LocalDateTime takenAt;

    @Column(name = "walk_img")
    private String walkImg;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "walk_id")
    private Walk walk;
}