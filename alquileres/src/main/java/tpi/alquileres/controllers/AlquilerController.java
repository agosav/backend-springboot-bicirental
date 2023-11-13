package tpi.alquileres.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tpi.alquileres.entities.dto.AlquilerDto;
import tpi.alquileres.entities.dto.IdClienteDto;
import tpi.alquileres.exceptions.ClienteConAlquilerActivoException;
import tpi.alquileres.services.AlquilerService;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/alquileres")
public class AlquilerController {

    @Autowired
    private AlquilerService service;

    // Consigna 6 (hecho)
    @GetMapping("/finalizados")
    private ResponseEntity<List<AlquilerDto>> getFinalizados(
            @RequestParam(name = "clienteId", required = false) String clienteId,
            @RequestParam(name = "estacionRetiroId", required = false) Long estacionRetiroId,
            @RequestParam(name = "estacionDevolucionId", required = false) Long estacionDevolucionId) {
        List<AlquilerDto> values = service.getFinalizados(clienteId, estacionRetiroId, estacionDevolucionId);
        return ResponseEntity.ok(values);
    }

    // Consigna 3 (hecho)
    @PostMapping("/iniciar/{estacionRetiroId}")
    private ResponseEntity<Void> iniciar(
            @PathVariable("estacionRetiroId") Long estacionId,
            @RequestBody IdClienteDto idClienteDto) {
        try {
            service.iniciar(estacionId, idClienteDto.getIdCliente());
            return ResponseEntity.status(HttpStatus.CREATED).build();

        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        } catch (ClienteConAlquilerActivoException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    // Consigna 4 /finalizar/00?moneda=00
    @PostMapping("/finalizar/{estacionDevolucionId}")
    private ResponseEntity<AlquilerDto> finalizar(
            @PathVariable("estacionDevolucionId") Long estacionId,
            @RequestParam(name = "moneda", required = false) String moneda,
            @RequestBody IdClienteDto idCliente) {
        try {
            AlquilerDto alquiler = service.finalizar(estacionId, idCliente.getIdCliente(), moneda);
            return ResponseEntity.ok(alquiler);

        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}

