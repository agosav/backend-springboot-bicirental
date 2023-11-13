package tpi.alquileres.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tarifas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tarifa {

    @Id
    @GeneratedValue(generator = "tarifas")
    @TableGenerator(name = "tarifas", table = "sqlite_sequence",
            pkColumnName = "name", valueColumnName = "seq",
            pkColumnValue = "id",
            initialValue = 1, allocationSize = 1)
    private long id;

    @Column(name = "tipo_tarifa")
    private int tipoTarifa;

    private String definicion;

    @Column(name = "dia_semana")
    private Integer diaSemana;

    @Column(name = "dia_mes")
    private Integer diaMes;

    private Integer mes;

    private Integer anio;

    @Column(name = "monto_fijo_alquiler")
    private double montoFijoAlquiler;

    @Column(name = "monto_minuto_fraccion")
    private double montoMinutoFraccion;

    @Column(name = "monto_km")
    private double montoKm;

    @Column(name = "monto_hora")
    private double montoHora;
}

