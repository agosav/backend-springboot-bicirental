package tpi.estaciones.services;

import org.springframework.stereotype.Service;
import tpi.estaciones.entities.Estacion;
import tpi.estaciones.entities.dto.EstacionDto;
import tpi.estaciones.repositories.EstacionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class EstacionServiceImpl implements EstacionService {

    private final EstacionRepository repo;

    public EstacionServiceImpl(EstacionRepository repo) {
        this.repo = repo;
    }

    @Override
    public void add(Estacion entity) {
        repo.save(entity);
    }

    @Override
    public void update(Estacion entity) {
    }

    @Override
    public Estacion delete(Long aLong) {
        return null;
    }

    @Override
    public Estacion getById(Long aLong) {
        Optional<Estacion> estacion = repo.findById(aLong);
        if (estacion.isPresent()) {
            return estacion.get();
        } else {
            throw new NoSuchElementException("No se encontró esa estación.");
        }
    }

    @Override
    public List<Estacion> getAll() {
        List<Estacion> estaciones = repo.findAll();
        return estaciones
                .stream()
                .toList();
    }

    public double calcularDistancia(Estacion estacion1, Estacion estacion2) {
        int grados = 110000;

        return Math.sqrt(
                Math.pow((estacion1.getLongitud() - estacion2.getLongitud()), 2) +
                        Math.pow((estacion1.getLatitud() - estacion2.getLatitud()), 2)) * grados;
    }

    @Override
    public Estacion encontrarEstacionMasCercana(double lat, double lon) {
        Estacion estacion1 = new Estacion();
        estacion1.setLatitud(lat);
        estacion1.setLongitud(lon);

        List<Estacion> estaciones = repo.findAll();

        Estacion estacionMasCercana = null;
        double distanciaMinima = Double.MAX_VALUE;

        for (Estacion estacion2 : estaciones) {
            double distancia = calcularDistancia(estacion1, estacion2);
            if (distancia < distanciaMinima) {
                distanciaMinima = distancia;
                estacionMasCercana = estacion2;
            }
        }

        assert estacionMasCercana != null;
        return estacionMasCercana;
    }

    public void addEstacionDto(EstacionDto estacionDto) {
        Estacion estacion = new Estacion();
        estacion.setNombre(estacionDto.getNombre());
        estacion.setLatitud(estacionDto.getLatitud());
        estacion.setLongitud(estacionDto.getLongitud());
        estacion.setFechaHoraCreacion(LocalDateTime.now());

        add(estacion);
    }
}


