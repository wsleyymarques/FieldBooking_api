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

    @Column(name = "document_id", length = 32)
    private String documentId;

    @Column(name = "avatar_url", length = 512)
    private String avatarUrl;

    @OneToOne(mappedBy = "profile", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Player player;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id",
            foreignKey = @ForeignKey(name = "fk_profiles_address"))
    private Address address;
}
