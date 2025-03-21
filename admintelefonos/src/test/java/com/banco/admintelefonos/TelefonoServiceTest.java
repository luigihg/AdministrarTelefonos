package com.banco.admintelefonos;

import com.banco.admintelefonos.model.Telefono;
import com.banco.admintelefonos.repository.TelefonoRepository;
import com.banco.admintelefonos.service.TelefonoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TelefonoServiceTest {

    @Mock
    private TelefonoRepository telefonoRepository;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private TelefonoService telefonoService;

    private Telefono telefono;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        telefono = new Telefono();
        telefono.setId("1");
        telefono.setNombre("Test Nombre");
        telefono.setMarca("Test Marca");
        telefono.setModelo("Test Modelo");
        telefono.setNombreCorto("Test Corto");
        telefono.setFechaCreacion(LocalDateTime.now());
        telefono.setImei("123456789012345");
        telefono.setEmailSoporte("test@example.com");

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void obtenerTelefonos_deberiaRetornarPaginaDeTelefonos() {
        Pageable pageable = Pageable.unpaged();
        List<Telefono> telefonos = Arrays.asList(telefono);
        Page<Telefono> page = new PageImpl<>(telefonos, pageable, telefonos.size());

        when(telefonoRepository.findAll(pageable)).thenReturn(page);

        Page<Telefono> result = telefonoService.obtenerTelefonos(pageable);

        assertEquals(page, result);
        verify(telefonoRepository, times(1)).findAll(pageable);
    }

    @Test
    void obtenerTelefonoPorId_deberiaRetornarTelefonoSiExiste() {
        when(telefonoRepository.findById("1")).thenReturn(Optional.of(telefono));

        Optional<Telefono> result = telefonoService.obtenerTelefonoPorId("1");

        assertTrue(result.isPresent());
        assertEquals(telefono, result.get());
        verify(telefonoRepository, times(1)).findById("1");
    }

    @Test
    void obtenerTelefonoPorId_deberiaRetornarOptionalVacioSiNoExiste() {
        when(telefonoRepository.findById("2")).thenReturn(Optional.empty());

        Optional<Telefono> result = telefonoService.obtenerTelefonoPorId("2");

        assertFalse(result.isPresent());
        verify(telefonoRepository, times(1)).findById("2");
    }

    @Test
    void obtenerTelefonoPorImei_deberiaRetornarTelefonoDeCacheSiExiste() throws Exception {
        String imei = "123456789012345";
        String cacheKey = "telefono:imei:" + imei;
        String telefonoJson = "{\"imei\":\"123456789012345\"}";

        when(valueOperations.get(cacheKey)).thenReturn(telefonoJson);
        when(objectMapper.readValue(telefonoJson, Telefono.class)).thenReturn(telefono);

        Optional<Telefono> result = telefonoService.obtenerTelefonoPorImei(imei);

        assertTrue(result.isPresent());
        assertEquals(telefono, result.get());
        verify(telefonoRepository, never()).findByImei(imei);
    }

    @Test
    void obtenerTelefonoPorImei_deberiaRetornarTelefonoDeRepositorioSiNoExisteEnCache() throws Exception {
        String imei = "123456789012345";
        String cacheKey = "telefono:imei:" + imei;
        String telefonoJson = "{\"imei\":\"123456789012345\"}";

        when(valueOperations.get(cacheKey)).thenReturn(null);
        when(telefonoRepository.findByImei(imei)).thenReturn(Optional.of(telefono));
        when(objectMapper.writeValueAsString(telefono)).thenReturn(telefonoJson);

        Optional<Telefono> result = telefonoService.obtenerTelefonoPorImei(imei);

        assertTrue(result.isPresent());
        assertEquals(telefono, result.get());
        verify(valueOperations, times(1)).set(cacheKey, telefonoJson);
    }

    @Test
    void guardarTelefono_deberiaGuardarTelefonoYInvalidarCache() throws Exception {
        when(telefonoRepository.save(telefono)).thenReturn(telefono);
        when(objectMapper.writeValueAsString(telefono)).thenReturn("{\"imei\":\"123456789012345\"}");

        Telefono result = telefonoService.guardarTelefono(telefono);

        assertEquals(telefono, result);
        verify(telefonoRepository, times(1)).save(telefono);
        verify(redisTemplate, times(1)).delete("telefono:imei:123456789012345");
        verify(kafkaTemplate, times(1)).send("telefono-cache-invalidation", "123456789012345");
    }

    @Test
    void eliminarTelefono_deberiaEliminarTelefonoYInvalidarCache() {
        when(telefonoRepository.findById("1")).thenReturn(Optional.of(telefono));

        telefonoService.eliminarTelefono("1");

        verify(telefonoRepository, times(1)).deleteById("1");
        verify(redisTemplate, times(1)).delete("telefono:imei:123456789012345");
        verify(kafkaTemplate, times(1)).send("telefono-cache-invalidation", "123456789012345");
    }
}