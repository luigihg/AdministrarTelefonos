package com.banco.admintelefonos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.banco.admintelefonos.model.Telefono;
import com.banco.admintelefonos.repository.TelefonoRepository;

import java.util.Optional;

@Service
public class TelefonoService {

    @Autowired
    private TelefonoRepository telefonoRepository;

    public Page<Telefono> obtenerTelefonos(Pageable pageable) {
        return telefonoRepository.findAll(pageable);
    }

    public Optional<Telefono> obtenerTelefonoPorId(String id) {
        return telefonoRepository.findById(id);
    }

    public Optional<Telefono> obtenerTelefonoPorImei(String imei) {
        return telefonoRepository.findByImei(imei);
    }

    public Telefono guardarTelefono(Telefono telefono) {
        Telefono telefonoGuardado = telefonoRepository.save(telefono);
        // Enviar mensaje a la cola (RabbitMQ/Kafka)
        // Implementar la lógica de envío aquí
        return telefonoGuardado;
    }

    public void eliminarTelefono(String id) {
        telefonoRepository.deleteById(id);
    }
}
