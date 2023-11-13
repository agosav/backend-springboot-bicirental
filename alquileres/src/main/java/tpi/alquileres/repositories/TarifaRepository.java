package tpi.alquileres.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tpi.alquileres.entities.Tarifa;

import java.util.List;
import java.util.Optional;

@Repository
public interface TarifaRepository extends JpaRepository<Tarifa, Long> {
    List<Tarifa> findByDefinicion(String definicion);
    Optional<Tarifa> findByDiaSemana(Integer dia);
}

