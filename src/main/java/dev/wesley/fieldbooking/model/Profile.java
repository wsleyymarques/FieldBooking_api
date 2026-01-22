package dev.wesley.fieldbooking.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile {

    @Id
    private UUID id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id",
            foreignKey = @ForeignKey(name = "fk_profiles_user"))
    private UserAccount user;

    private LocalDate birthDate;

    @Column(name = "avatar_url", length = 512)
    private String avatarUrl;

    @Column(length = 500)
    private String bio;

    @OneToOne(mappedBy = "profile", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Player player;

    @OneToMany(mappedBy = "profile", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<Address> addresses = new java.util.ArrayList<>();
}
