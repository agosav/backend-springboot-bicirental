package tpi.alquileres.services.mappers;

import org.springframework.stereotype.Service;
import tpi.alquileres.entities.Alquiler;
import tpi.alquileres.entities.dto.AlquilerDto;

import java.util.function.Function;

@Service
public class AlquilerDtoMapper implements Function<Alquiler, AlquilerDto> {
    @Override
    public AlquilerDto apply(Alquiler a) {
        return new AlquilerDto(
                a.getIdCliente(),
                a.getEstacionRetiro().getNombre(),
                a.getEstacionDevolucion().getNombre(),
                a.getFechaHoraRetiro(),
                a.getFechaHoraDevolucion(),
                a.getMonto()
        );
    }
}
