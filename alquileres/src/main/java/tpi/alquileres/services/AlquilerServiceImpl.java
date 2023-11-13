package tpi.alquileres.services;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import tpi.alquileres.entities.Alquiler;
import tpi.alquileres.entities.Estacion;
import tpi.alquileres.entities.Tarifa;
import tpi.alquileres.entities.dto.AlquilerDto;
import tpi.alquileres.entities.dto.MonedaRequest;
import tpi.alquileres.entities.dto.MonedaResponse;
import tpi.alquileres.exceptions.ClienteConAlquilerActivoException;
import tpi.alquileres.exceptions.NoTarifasDefinidasException;
import tpi.alquileres.repositories.AlquilerRepository;
import tpi.alquileres.repositories.TarifaRepository;
import tpi.alquileres.services.mappers.AlquilerDtoMapper;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AlquilerServiceImpl implements AlquilerService {

    private final AlquilerRepository alquilerRepository;
    private final TarifaRepository tarifaRepository;
    private final AlquilerDtoMapper mapper;

    public AlquilerServiceImpl(AlquilerRepository alquilerRepository, TarifaRepository tarifaRepository, AlquilerDtoMapper mapper) {
        this.alquilerRepository = alquilerRepository;
        this.tarifaRepository = tarifaRepository;
        this.mapper = mapper;
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
    public List<AlquilerDto> getFinalizados(String clienteId, Long estacionRetiroId, Long estacionDevolucionId) {

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
                .map(mapper)
                .toList();
    }

    @Override
    public void iniciar(Long estacionRetiroId, String idCliente) {

        Estacion estacionRetiro;

        // Consultar en el microservicio si existe una estación con la id ingresada
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

        // Validar que no existan alquileres activos para el cliente ingresado
        Optional<Alquiler> optionalAlquiler = alquilerRepository.findByIdClienteEstadoIniciado(idCliente);

        if (optionalAlquiler.isPresent()) {
            throw new ClienteConAlquilerActivoException("El cliente " + idCliente + " ya tiene un alquiler activo");
        }

        // Crear un alquiler
        Alquiler nuevo = new Alquiler();
        nuevo.setEstado(1);
        nuevo.setIdCliente(idCliente);
        nuevo.setEstacionRetiro(estacionRetiro);
        nuevo.setFechaHoraRetiro(LocalDateTime.now());

        add(nuevo);
    }

    public double convertirMoneda(String monedaDestino, double importe) {
        // MonedaRequest tiene el formato {"moneda_destino": "string", "importe": 0}
        // MonedaResponse tiene el formato {"moneda": "string", "importe": 0}
        try {
            RestTemplate template = new RestTemplate();

            MonedaRequest requestBody = new MonedaRequest(monedaDestino, importe);
            HttpEntity<MonedaRequest> entity = new HttpEntity<>(requestBody);

            ResponseEntity<MonedaResponse> res = template.postForEntity(
                    "http://34.82.105.125:8080/convertir", entity, MonedaResponse.class
            );

            if (res.getStatusCode().is2xxSuccessful()){
                return Objects.requireNonNull(res.getBody()).getImporte();
            } else {
                throw new NoSuchElementException("Error al buscar la moneda");
            }
        } catch (HttpClientErrorException e) {
            throw new NoSuchElementException("Error al procesar la moneda");
        }
    }

    @Override
    public AlquilerDto finalizar(Long estacionDevolucionId, String idCliente, String moneda) {

        Estacion estacion;

        // Consultar en el microservicio si existe una estación con la id ingresada
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

        // Validar que exista un alquiler para el cliente ingresado
        Optional<Alquiler> optionalAlquiler = alquilerRepository.findByIdClienteEstadoIniciado(idCliente);

        if (optionalAlquiler.isEmpty()) {
            throw new NoSuchElementException("No se encontró el alquiler del cliente " + idCliente);
        }

        // Actualizar datos del alquiler
        Alquiler alquiler = optionalAlquiler.get();

        alquiler.setEstado(2);
        alquiler.setFechaHoraDevolucion(LocalDateTime.now());
        alquiler.setEstacionDevolucion(estacion);
        alquiler.setTarifa(determinarTarifa(alquiler));
        double monto = calcularMonto(alquiler);
        if (moneda != null && !moneda.isEmpty()) {
            try {
                monto = convertirMoneda(moneda, monto);
            } catch (NoSuchElementException e) {
                throw new NoSuchElementException("No se encontró la moneda " + moneda);
            }
        }
        alquiler.setMonto(monto);

        update(alquiler);

        return mapper.apply(alquiler);
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
