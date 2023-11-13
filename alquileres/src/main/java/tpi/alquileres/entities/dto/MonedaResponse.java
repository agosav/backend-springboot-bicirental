package tpi.alquileres.entities.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonedaResponse {
    private String moneda;
    private double importe;
}
