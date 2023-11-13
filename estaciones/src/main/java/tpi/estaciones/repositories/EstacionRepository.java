package tpi.estaciones.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tpi.estaciones.entities.Estacion;

@Repository
public interface EstacionRepository extends JpaRepository<Estacion, Long> {
}
