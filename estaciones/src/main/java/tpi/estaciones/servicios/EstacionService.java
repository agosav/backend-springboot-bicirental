package tpi.estaciones.servicios;

import tpi.estaciones.entidades.Estacion;
import tpi.estaciones.entidades.dto.EstacionDto;

public interface EstacionService extends Service<Estacion, Long>{
    Estacion encontrarEstacionMasCercana(double lat, double lon);
    double calcularDistancia(Estacion e1, Estacion e2);
    void addEstacionDto(EstacionDto estacionDto);
}

