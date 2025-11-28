package dev.wesley.fieldbooking.model;

import dev.wesley.fieldbooking.model.Enums.Position;
import dev.wesley.fieldbooking.model.Enums.SkillLevel;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "players")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Player {

    @Id
    private UUID id; // mesmo id do User

    @OneToOne
    @MapsId
    @JoinColumn(name = "id",
            foreignKey = @ForeignKey(name = "fk_players_user"))
    private UserAccount user;

    @Enumerated(EnumType.STRING)
    @Column(name = "skill_level", nullable = false, length = 20)
    @Builder.Default
    private SkillLevel skillLevel = SkillLevel.BEGINNER;

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_position", length = 20)
    private Position preferredPosition;

    @Column(name = "rating", precision = 2, scale = 1) // ex: 4.5
    private BigDecimal rating; // <-- trocado para BigDecimal

    @Column(length = 500)
    private String bio;
}
