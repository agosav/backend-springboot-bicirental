package tpi.alquileres.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tpi.alquileres.entities.Alquiler;
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

    // No es ninguna consigna, es solo para ir viendo los alquileres (esto anda bien)
    @GetMapping
    private ResponseEntity<List<Alquiler>> getAll() {
        List<Alquiler> values = service.getAll();
        return ResponseEntity.ok(values);
    }

    // Consigna 6 (hecho)
    @GetMapping("/finalizados")
    private ResponseEntity<List<Alquiler>> getFinalizados(
            @RequestParam(name = "clienteId", required = false) String clienteId,
            @RequestParam(name = "estacionRetiroId", required = false) Long estacionRetiroId,
            @RequestParam(name = "estacionDevolucionId", required = false) Long estacionDevolucionId) {
        List<Alquiler> values = service.getFinalizados(clienteId, estacionRetiroId, estacionDevolucionId);
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

    // Consigna 4 (hecho pero le falta lo del tipo de cambio)
    @PostMapping("/finalizar/{estacionDevolucionId}")
    private ResponseEntity<Alquiler> finalizar(
            @PathVariable("estacionDevolucionId") Long estacionId,
            @RequestBody IdClienteDto idClienteDto) {
        try {
            Alquiler alquiler = service.finalizar(estacionId, idClienteDto.getIdCliente());
            return ResponseEntity.ok(alquiler);

        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}

