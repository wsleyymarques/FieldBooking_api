package dev.wesley.fieldbooking.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity @Table(name = "managers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Manager {

    @Id
    private UUID id; // mesmo id do User

    @OneToOne
    @MapsId
    @JoinColumn(name = "id",
            foreignKey = @ForeignKey(name = "fk_managers_user"))
    private UserAccount user;

    @Column(name = "venue_name", length = 160, nullable = false)
    private String venueName; // nome do local/quadra

    @Column(name = "tax_id", length = 32)
    private String taxId;     // CNPJ/empresa (opcional)

    @Column(name = "contact_phone", length = 30)
    private String contactPhone;

    @Column(name = "address_line", length = 200)
    private String addressLine;

    @Column(name = "verified", nullable = false)
    private boolean verified;
}
