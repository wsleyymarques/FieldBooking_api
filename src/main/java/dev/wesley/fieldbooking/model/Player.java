package dev.wesley.fieldbooking.model;

import dev.wesley.fieldbooking.model.Enums.Position;
import dev.wesley.fieldbooking.model.Enums.SkillLevel;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "players")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Player {

    @Id
    private UUID id; // mesmo id do Profile

    @OneToOne
    @MapsId
    @JoinColumn(name = "id",
            foreignKey = @ForeignKey(name = "fk_players_profile"))
    private Profile profile;

    @Enumerated(EnumType.STRING)
    @Column(name = "skill_level", nullable = false, length = 20)
    @Builder.Default
    private SkillLevel skillLevel = SkillLevel.BEGINNER;

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_position", length = 20)
    private Position preferredPosition;

    @Column(name = "rating", precision = 2, scale = 1) // ex: 4.5
    private BigDecimal rating;

    @Column(name = "goals", nullable = false)
    @Builder.Default
    private Integer goals = 0;

    @Column(name = "assists", nullable = false)
    @Builder.Default
    private Integer assists = 0;

    @Column(name = "matches_played", nullable = false)
    @Builder.Default
    private Integer matchesPlayed = 0;
}
