package tpi.alquileres.services;

import tpi.alquileres.entities.Alquiler;

import java.util.List;

public interface AlquilerService extends Service<Alquiler, Long>{
    List<Alquiler> getFinalizados(String clienteId, Long estacionRetiroId, Long estacionDevolucionId);
    Alquiler finalizar(Long estacionDevolucionId, String clienteId);
    void iniciar(Long estacionRetiroId, String clienteId);
}

