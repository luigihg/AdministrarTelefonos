package com.banco.admintelefonos;

import com.banco.admintelefonos.model.Telefono;
import com.banco.admintelefonos.repository.TelefonoRepository;
import com.banco.admintelefonos.service.TelefonoService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TelefonoServiceTest {

    @Mock
    private TelefonoRepository telefonoRepository;

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
    void obtenerTelefonoPorImei_deberiaRetornarTelefonoSiExiste() {
        when(telefonoRepository.findByImei("123456789012345")).thenReturn(Optional.of(telefono));

        Optional<Telefono> result = telefonoService.obtenerTelefonoPorImei("123456789012345");

        assertTrue(result.isPresent());
        assertEquals(telefono, result.get());
        verify(telefonoRepository, times(1)).findByImei("123456789012345");
    }

    @Test
    void obtenerTelefonoPorImei_deberiaRetornarOptionalVacioSiNoExiste() {
        when(telefonoRepository.findByImei("999999999999999")).thenReturn(Optional.empty());

        Optional<Telefono> result = telefonoService.obtenerTelefonoPorImei("999999999999999");

        assertFalse(result.isPresent());
        verify(telefonoRepository, times(1)).findByImei("999999999999999");
    }

    @Test
    void guardarTelefono_deberiaGuardarTelefonoYRetornarTelefonoGuardado() {
        when(telefonoRepository.save(telefono)).thenReturn(telefono);

        Telefono result = telefonoService.guardarTelefono(telefono);

        assertEquals(telefono, result);
        verify(telefonoRepository, times(1)).save(telefono);
    }

    @Test
    void eliminarTelefono_deberiaEliminarTelefono() {
        telefonoService.eliminarTelefono("1");

        verify(telefonoRepository, times(1)).deleteById("1");
    }
}
