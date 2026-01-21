package dev.wesley.fieldbooking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Size(max = 2)
    @Column(length = 2)
    private String country;

    @Size(max = 120)
    @Column(length = 120)
    private String street;

    @Size(max = 20)
    @Column(length = 20)
    private String number;

    @Size(max = 120)
    @Column(length = 120)
    private String neighborhood;

    @Size(max = 120)
    @Column(length = 120)
    private String city;

    @Size(max = 2)
    @Column(length = 2)
    private String state; // UF

    @Size(max = 12)
    @Column(name = "zip_code", length = 12)
    private String zipCode; // CEP

    @Size(max = 120)
    @Column(length = 120)
    private String complement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", foreignKey = @ForeignKey(name = "fk_addresses_store"))
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", foreignKey = @ForeignKey(name = "fk_addresses_profile"))
    private Profile profile;

    private Double latitude;
    private Double longitude;
}
