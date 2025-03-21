package com.banco.admintelefonos.service;

import com.banco.admintelefonos.model.Telefono;
import com.banco.admintelefonos.repository.TelefonoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TelefonoService {

    @Autowired
    private TelefonoRepository telefonoRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String TELEFONO_CACHE_PREFIX = "telefono:imei:";
    private static final String TELEFONO_CACHE_INVALIDATION_TOPIC = "telefono-cache-invalidation";

    public Page<Telefono> obtenerTelefonos(Pageable pageable) {
        return telefonoRepository.findAll(pageable);
    }

    public Optional<Telefono> obtenerTelefonoPorId(String id) {
        return telefonoRepository.findById(id);
    }

    public Optional<Telefono> obtenerTelefonoPorImei(String imei) {
        String cacheKey = TELEFONO_CACHE_PREFIX + imei;
        String cachedTelefonoJson = redisTemplate.opsForValue().get(cacheKey);

        if (cachedTelefonoJson != null) {
            try {
                Telefono cachedTelefono = objectMapper.readValue(cachedTelefonoJson, Telefono.class);
                return Optional.of(cachedTelefono);
            } catch (Exception e) {
                // Manejar la excepción si falla la deserialización
                e.printStackTrace();
            }
        }

        Optional<Telefono> telefono = telefonoRepository.findByImei(imei);
        telefono.ifPresent(t -> {
            try {
                String telefonoJson = objectMapper.writeValueAsString(t);
                redisTemplate.opsForValue().set(cacheKey, telefonoJson);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return telefono;
    }

    public Telefono guardarTelefono(Telefono telefono) {
        Telefono telefonoGuardado = telefonoRepository.save(telefono);
        if (telefonoGuardado != null && telefonoGuardado.getImei() != null) {
            invalidarCache(telefonoGuardado.getImei());
            System.out.println("Se debe de probar la conexion a redis");
        }
        return telefonoGuardado;
    }

    public void eliminarTelefono(String id) {
        Optional<Telefono> telefono = telefonoRepository.findById(id);
        telefono.ifPresent(t -> {
            if (t.getImei() != null) {
                invalidarCache(t.getImei());
            }
        });
        telefonoRepository.deleteById(id);
    }

    private void invalidarCache(String imei) {
        String cacheKey = TELEFONO_CACHE_PREFIX + imei;
        redisTemplate.delete(cacheKey);
        kafkaTemplate.send(TELEFONO_CACHE_INVALIDATION_TOPIC, imei);
    }
}