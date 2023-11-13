package tpi.estaciones.entities.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstacionDto {
    private String nombre;
    private double latitud;
    private double longitud;
}

