package tpi.estaciones.services;

import tpi.estaciones.entities.Estacion;
import tpi.estaciones.entities.dto.EstacionDto;

public interface EstacionService extends Service<Estacion, Long>{
    Estacion encontrarEstacionMasCercana(double lat, double lon);
    double calcularDistancia(Estacion e1, Estacion e2);
    void addEstacionDto(EstacionDto estacionDto);
}

