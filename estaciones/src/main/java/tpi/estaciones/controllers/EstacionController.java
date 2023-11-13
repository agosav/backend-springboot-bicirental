package tpi.estaciones.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tpi.estaciones.entities.Estacion;
import tpi.estaciones.entities.dto.EstacionDto;
import tpi.estaciones.services.EstacionService;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/estaciones")
public class EstacionController {

    @Autowired
    private EstacionService service;

    // Consigna 1 (hecha)
    @GetMapping
    private ResponseEntity<List<Estacion>> getAll() {
        List<Estacion> values = service.getAll();
        return ResponseEntity.ok(values);
    }

    // Get by Id
    @GetMapping("/{id}")
    private ResponseEntity<Estacion> getById(@PathVariable Long id) {
        try {
            Estacion estacion = service.getById(id);
            return ResponseEntity.ok(estacion);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Consigna 2 (hecha)
    @GetMapping("/cercana")
    private ResponseEntity<Estacion> buscarCercana(
            @RequestParam("lat") double lat,
            @RequestParam("lon") double lon) {
        Estacion estacion = service.encontrarEstacionMasCercana(lat, lon);
        return ResponseEntity.ok(estacion);
    }

    // Consigna 5 (hecha)
    @PostMapping
    private ResponseEntity<Void> addEstacion(@RequestBody EstacionDto entity) {
        service.addEstacionDto(entity);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}

