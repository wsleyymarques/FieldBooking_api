// src/main/java/com/example/domain/field/Field.java
package dev.wesley.fieldbooking.model;

import dev.wesley.fieldbooking.model.Enums.FieldStatus;
import dev.wesley.fieldbooking.model.Enums.FieldType;
import dev.wesley.fieldbooking.model.Enums.Surface;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Entity @Table(name = "fields",
        indexes = {@Index(name="idx_field_store", columnList = "store_id"),
                @Index(name="idx_field_name", columnList = "name")})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Field {

    @Id @GeneratedValue @UuidGenerator
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @NotBlank @Size(max = 120)
    private String name; // ex.: “Quadra 1”

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private FieldType type; // society, futsal, vôlei...

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Surface surface; // gramado, sintético, cimento...

    @Positive @Column(precision = 10, scale = 2)
    private BigDecimal pricePerHour;

    // dimensões livres (ex.: 30x50) — opcional
    @Size(max = 40)
    private String sizeLabel;

    private boolean indoor;    // coberta?
    private boolean lighting;  // tem iluminação?
    private boolean lockerRoom;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    @Builder.Default
    private FieldStatus status = FieldStatus.AVAILABLE;
}

