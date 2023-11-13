package tpi.alquileres.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import tpi.alquileres.entities.Alquiler;
import tpi.alquileres.entities.Estacion;
import tpi.alquileres.entities.Tarifa;
import tpi.alquileres.exceptions.ClienteConAlquilerActivoException;
import tpi.alquileres.exceptions.NoTarifasDefinidasException;
import tpi.alquileres.repositories.AlquilerRepository;
import tpi.alquileres.repositories.TarifaRepository;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AlquilerServiceImpl implements AlquilerService {

    private final AlquilerRepository alquilerRepository;
    private final TarifaRepository tarifaRepository;

    public AlquilerServiceImpl(AlquilerRepository alquilerRepository, TarifaRepository tarifaRepository) {
        this.alquilerRepository = alquilerRepository;
        this.tarifaRepository = tarifaRepository;
    }

    @Override
    public void add(Alquiler entity) {
        alquilerRepository.save(entity);
    }

    @Override
    public void update(Alquiler entity) {
        alquilerRepository.save(entity);
    }

    @Override
    public Alquiler delete(Long aLong) {
        return null;
    }

    @Override
    public Alquiler getById(Long aLong) {
        return null;
    }

    @Override
    public List<Alquiler> getAll() {
        List<Alquiler> alquileres = alquilerRepository.findAll();
        return alquileres
                .stream()
                .toList();
    }

    @Override
    public List<Alquiler> getFinalizados(String clienteId, Long estacionRetiroId, Long estacionDevolucionId) {

        List<Alquiler> alquileres = alquilerRepository.findByEstado(2);

        // Filtrar por clienteId
        if (clienteId != null && !clienteId.isEmpty()) {
            alquileres = alquileres.stream()
                    .filter(alquiler -> alquiler.getIdCliente() != null &&
                            alquiler.getIdCliente().equals(clienteId))
                    .collect(Collectors.toList());
        }

        // Filtrar por estación de retiro
        if (estacionRetiroId != null) {
            alquileres = alquileres.stream()
                    .filter(alquiler -> alquiler.getEstacionRetiro() != null &&
                            alquiler.getEstacionRetiro().getId() == estacionRetiroId)
                    .collect(Collectors.toList());
        }

        // Filtrar por estación de devolución
        if (estacionDevolucionId != null) {
            alquileres = alquileres.stream()
                    .filter(alquiler -> alquiler.getEstacionDevolucion() != null &&
                            alquiler.getEstacionDevolucion().getId() == estacionDevolucionId)
                    .collect(Collectors.toList());
        }

        return alquileres
                .stream()
                .toList();
    }

    @Override
    public void iniciar(Long estacionRetiroId, String idCliente) {

        Estacion estacionRetiro;

        try {
            RestTemplate template = new RestTemplate();
            ResponseEntity<Estacion> res = template.getForEntity(
                    "http://localhost:8081/api/estaciones/{id}", Estacion.class, estacionRetiroId
            );

            if (res.getStatusCode().is2xxSuccessful()) {
                estacionRetiro = res.getBody();
            } else {
                throw new NoSuchElementException("No se encontró esa estación");
            }
        } catch (HttpClientErrorException e) {
            throw new NoSuchElementException("Error al buscar la estación");
        }

        Optional<Alquiler> optionalAlquiler = alquilerRepository.findByIdClienteEstadoIniciado(idCliente);

        if (optionalAlquiler.isPresent()) {
            throw new ClienteConAlquilerActivoException("El cliente " + idCliente + " ya tiene un alquiler activo");
        }

        Alquiler nuevo = new Alquiler();
        nuevo.setEstado(1);
        nuevo.setIdCliente(idCliente);
        nuevo.setEstacionRetiro(estacionRetiro);
        nuevo.setFechaHoraRetiro(LocalDateTime.now());

        add(nuevo);
    }

    @Override
    public Alquiler finalizar(Long estacionDevolucionId, String idCliente) {

        Estacion estacion;

        try {
            RestTemplate template = new RestTemplate();
            ResponseEntity<Estacion> res = template.getForEntity(
                    "http://localhost:8081/api/estaciones/{id}", Estacion.class, estacionDevolucionId
            );

            if (res.getStatusCode().is2xxSuccessful()) {
                estacion = res.getBody();
            } else {
                throw new NoSuchElementException("No se encontró esa estación");
            }
        } catch (HttpClientErrorException e) {
            throw new NoSuchElementException("Error al buscar la estación");
        }

        Optional<Alquiler> optionalAlquiler = alquilerRepository.findByIdClienteEstadoIniciado(idCliente);

        if (optionalAlquiler.isEmpty()) {
            throw new NoSuchElementException("No se encontró el alquiler del cliente " + idCliente);
        }

        Alquiler alquiler = optionalAlquiler.get();

        alquiler.setEstado(2);
        alquiler.setFechaHoraDevolucion(LocalDateTime.now());
        alquiler.setEstacionDevolucion(estacion);
        alquiler.setTarifa(determinarTarifa(alquiler));
        alquiler.setMonto(calcularMonto(alquiler));

        update(alquiler);

        return alquiler;
    }

    public Tarifa determinarTarifa(Alquiler alquiler) {
        LocalDateTime fecha = alquiler.getFechaHoraDevolucion();

        // Primero checkeamos si alguna tarifa definida por día, mes y año coincide con la fecha de devolución
        List<Tarifa> tarifas = tarifaRepository.findByDefinicion("C");
        for (Tarifa t : tarifas) {
            if (t.getDiaMes() == fecha.getDayOfMonth() &&
                    t.getMes() == fecha.getMonthValue() &&
                    t.getAnio() == fecha.getYear()) {
                return t;
            }
        }

        // Si no coincide ninguna, entonces buscamos la tarifa correspondiente definida por el día de la semana
        Optional<Tarifa> optionalTarifa = tarifaRepository.findByDiaSemana(fecha.getDayOfWeek().getValue());

        if (optionalTarifa.isPresent()) {
            return optionalTarifa.get();
        }

        throw new NoTarifasDefinidasException("No se encontraron tarifas definidas.");
    }

    public double calcularMonto(Alquiler alq) {
        Tarifa tarifa = alq.getTarifa();
        double monto;

        double lat1 = alq.getEstacionRetiro().getLatitud();
        double lon1 = alq.getEstacionRetiro().getLongitud();
        double lat2 = alq.getEstacionDevolucion().getLatitud();
        double lon2 = alq.getEstacionDevolucion().getLongitud();
        long distanciaEnKilometros = (long) Math.floor(Math.sqrt(Math.pow(lat1 - lat2, 2) + Math.pow(lon1 - lon2, 2)) * 110);
        long diferenciaEnMinutos = Duration.between(alq.getFechaHoraRetiro(), alq.getFechaHoraDevolucion()).toMinutes();

        long horasCompletas = diferenciaEnMinutos / 60;
        long minutosRestantes = diferenciaEnMinutos % 60;

        monto = tarifa.getMontoFijoAlquiler();
        monto += distanciaEnKilometros * tarifa.getMontoKm();
        monto += horasCompletas * tarifa.getMontoHora();

        if (minutosRestantes > 30) {
            monto += tarifa.getMontoHora();
        } else {
            monto += minutosRestantes * tarifa.getMontoMinutoFraccion();
        }

        return monto;
    }
}
