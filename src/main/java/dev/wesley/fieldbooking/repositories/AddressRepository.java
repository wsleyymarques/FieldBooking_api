package dev.wesley.fieldbooking.repositories;

import dev.wesley.fieldbooking.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {

    // Busca por cidade (case-insensitive)
    List<Address> findByCityIgnoreCase(String city);

    // Encontra endereços dentro de um raio (metros) do ponto lat/lon, ordenados pela distância asc
    @Query(
            value = """
        SELECT a.*
        FROM addresses a
        WHERE ST_DWithin(
          a.geom::geography,
          ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography,
          :radiusMeters
        )
        ORDER BY ST_Distance(
          a.geom::geography,
          ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography
        )
        """,
            nativeQuery = true
    )
    List<Address> findWithinDistance(
            @Param("lon") double lon,
            @Param("lat") double lat,
            @Param("radiusMeters") double radiusMeters
    );

    // Encontra N endereços mais próximos (limite aplicado pela query)
    @Query(
            value = """
        SELECT a.*
        FROM addresses a
        ORDER BY a.geom <-> ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)
        LIMIT :limit
        """,
            nativeQuery = true
    )
    List<Address> findNearest(
            @Param("lon") double lon,
            @Param("lat") double lat,
            @Param("limit") int limit
    );
}
