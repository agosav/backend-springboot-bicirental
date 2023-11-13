package tpi.alquileres.entities.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonedaRequest {
    @JsonProperty("moneda_destino")
    private String monedaDestino;
    private double importe;
}
