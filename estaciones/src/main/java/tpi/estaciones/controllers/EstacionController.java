package tpi.estaciones.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tpi.estaciones.entidades.Estacion;
import tpi.estaciones.entidades.dto.EstacionDto;
import tpi.estaciones.servicios.EstacionService;

import java.util.List;

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

