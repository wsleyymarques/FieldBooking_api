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
    private UUID id; // mesmo id do User

    @OneToOne
    @MapsId               // compartilha a PK com users.id
    @JoinColumn(name = "id",
            foreignKey = @ForeignKey(name = "fk_profiles_user"))
    private UserAccount user;

    @Column(name = "first_name", length = 80)
    private String firstName;

    @Column(name = "last_name", length = 120)
    private String lastName;

    private LocalDate birthDate;

    @Column(name = "document_id", length = 32)
    private String documentId; // CPF/CNI, etc. (se quiser tirar, a gente tira depois)

    @Column(name = "avatar_url", length = 512)
    private String avatarUrl;

    /** Player opcional vinculado ao Profile */
    @OneToOne(mappedBy = "profile", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Player player;

    /** Address opcional vinculado ao Profile */
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id",
            foreignKey = @ForeignKey(name = "fk_profiles_address"))
    private Address address;
}
