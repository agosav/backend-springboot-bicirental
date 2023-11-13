package tpi.alquileres.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "estaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Estacion {
    @Id
    @GeneratedValue(generator = "estaciones")
    @TableGenerator(name = "estaciones", table = "sqlite_sequence",
            pkColumnName = "name", valueColumnName = "seq",
            pkColumnValue="id",
            initialValue = 1, allocationSize = 1)
    private long id;

    private String nombre;

    @Column(name = "fecha_hora_creacion")
    private LocalDateTime fechaHoraCreacion;

    private double latitud;

    private double longitud;

}
