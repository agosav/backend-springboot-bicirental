package tpi.alquileres.services;

import tpi.alquileres.entities.Alquiler;
import tpi.alquileres.entities.dto.AlquilerDto;

import java.util.List;

public interface AlquilerService extends Service<Alquiler, Long>{
    List<AlquilerDto> getFinalizados(String clienteId, Long estacionRetiroId, Long estacionDevolucionId);
    AlquilerDto finalizar(Long estacionDevolucionId, String clienteId, String moneda);
    void iniciar(Long estacionRetiroId, String clienteId);
}

