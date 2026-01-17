package dev.wesley.fieldbooking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.annotations.Type;
import org.locationtech.jts.geom.Point;

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

    private Double latitude;
    private Double longitude;

    /**
     * Geography column (PostGIS). Stored as SRID 4326 (lon/lat).
     * columnDefinition uses `geography` to allow distance in meters easily.
     */
    @Column(columnDefinition = "geography(Point,4326)")
    private Point geom;

    // Helper convenience setters to keep lat/lon and geom in sync (see service example).
}
