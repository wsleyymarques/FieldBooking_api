// src/main/java/com/fieldbooking/domain/store/Store.java
package dev.wesley.fieldbooking.model;

import dev.wesley.fieldbooking.model.Enums.StoreAmenity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.OffsetDateTime;
import java.util.*;

@Entity @Table(name = "stores",
        indexes = {@Index(name="idx_store_name", columnList = "name")})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Store {

    @Id @GeneratedValue @UuidGenerator
    @Column(columnDefinition = "uuid")
    private UUID id;

    @NotBlank @Size(max = 120)
    private String name;

    @Size(max = 18)
    @Column(unique = true)
    private String cnpj;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(
            name = "address_id",
            foreignKey = @ForeignKey(name = "fk_stores_address")
    )
    private Address address;

    @Size(max = 120) private String contactEmail;
    @Size(max = 20)  private String contactPhone;
    @Size(max = 120) private String openingHours;

    @Builder.Default
    private Boolean active = true;

    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

    /** Amenities/Features do estabelecimento */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "store_amenities",
            joinColumns = @JoinColumn(name = "store_id",
                    foreignKey = @ForeignKey(name = "fk_store_amenities_store")))
    @Enumerated(EnumType.STRING)
    @Column(name = "amenity", length = 40, nullable = false)
    @Builder.Default
    private Set<StoreAmenity> amenities = new HashSet<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Field> fields = new ArrayList<>();

    public void addField(Field f) { f.setStore(this); this.fields.add(f); }
    public void removeField(Field f) { f.setStore(null); this.fields.remove(f); }
}
