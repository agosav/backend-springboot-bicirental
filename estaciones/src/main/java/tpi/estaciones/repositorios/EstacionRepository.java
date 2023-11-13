package tpi.estaciones.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tpi.estaciones.entidades.Estacion;

@Repository
public interface EstacionRepository extends JpaRepository<Estacion, Long> {
}
