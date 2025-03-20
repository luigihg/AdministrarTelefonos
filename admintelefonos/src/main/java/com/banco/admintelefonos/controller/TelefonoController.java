package com.banco.admintelefonos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.banco.admintelefonos.model.Telefono;
import com.banco.admintelefonos.service.TelefonoService;

import java.util.Optional;

@RestController
@RequestMapping("/telefonos")
public class TelefonoController {

    @Autowired
    private TelefonoService telefonoService;

    @GetMapping
    public ResponseEntity<Page<Telefono>> obtenerTelefonos(Pageable pageable) {
        return ResponseEntity.ok(telefonoService.obtenerTelefonos(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Telefono> obtenerTelefonoPorId(@PathVariable String id) {
        Optional<Telefono> telefono = telefonoService.obtenerTelefonoPorId(id);
        return telefono.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/imei/{imei}")
    public ResponseEntity<Telefono> obtenerTelefonoPorImei(@PathVariable String imei) {
        Optional<Telefono> telefono = telefonoService.obtenerTelefonoPorImei(imei);
        return telefono.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Telefono> crearTelefono(@RequestBody Telefono telefono) {
        Telefono nuevoTelefono = telefonoService.guardarTelefono(telefono);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoTelefono);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Telefono> actualizarTelefono(@PathVariable String id, @RequestBody Telefono telefono) {
        Optional<Telefono> telefonoExistente = telefonoService.obtenerTelefonoPorId(id);
        if (telefonoExistente.isPresent()) {
            telefono.setId(id);
            Telefono telefonoActualizado = telefonoService.guardarTelefono(telefono);
            return ResponseEntity.ok(telefonoActualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTelefono(@PathVariable String id) {
        Optional<Telefono> telefono = telefonoService.obtenerTelefonoPorId(id);
        if (telefono.isPresent()) {
            telefonoService.eliminarTelefono(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}