package tpi.alquileres.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tpi.alquileres.entities.Alquiler;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlquilerRepository extends JpaRepository<Alquiler, Long> {
    List<Alquiler> findByEstado(int estado);

    // Nunca debería haber más de un alquiler activo por cliente, por lo tanto la función devuelve un solo objeto
    // Alquiler en lugar de una lista de Alquileres.
    @Query("SELECT a FROM Alquiler a WHERE a.idCliente = :idCliente AND a.estado = 1")
    Optional<Alquiler> findByIdClienteEstadoIniciado(String idCliente);
}

