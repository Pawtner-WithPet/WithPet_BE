package com.pawtner.WithPet.domain.walk.entity;

import com.pawtner.WithPet.domain.pet.entity.Pet;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "walk_pet")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalkPet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "walk_pet_id")
    private Long walkPetId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "walk_id")
    private Walk walk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id")
    private Pet pet;
}