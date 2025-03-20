package com.banco.admintelefonos;

import com.banco.admintelefonos.controller.TelefonoController;
import com.banco.admintelefonos.dto.ErrorResponse;
import com.banco.admintelefonos.enums.ErrorCode;
import com.banco.admintelefonos.model.Telefono;
import com.banco.admintelefonos.service.TelefonoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TelefonoControllerTest {

    @Mock
    private TelefonoService telefonoService;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private TelefonoController telefonoController;

    private Telefono telefono;
    private Locale locale;

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
        locale = Locale.getDefault();
    }

    @Test
    void obtenerTelefonos_deberiaRetornarPaginaDeTelefonos() {
        Pageable pageable = Pageable.unpaged();
        List<Telefono> telefonos = Arrays.asList(telefono);
        Page<Telefono> page = new PageImpl<>(telefonos, pageable, telefonos.size());

        when(telefonoService.obtenerTelefonos(pageable)).thenReturn(page);

        ResponseEntity<Page<Telefono>> response = telefonoController.obtenerTelefonos(pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void obtenerTelefonoPorId_deberiaRetornarTelefonoExistente() {
        when(telefonoService.obtenerTelefonoPorId("1")).thenReturn(Optional.of(telefono));

        ResponseEntity<Telefono> response = telefonoController.obtenerTelefonoPorId("1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(telefono, response.getBody());
    }

    @Test
    void obtenerTelefonoPorId_deberiaRetornarNotFoundSiNoExiste() {
        when(telefonoService.obtenerTelefonoPorId("2")).thenReturn(Optional.empty());

        ResponseEntity<Telefono> response = telefonoController.obtenerTelefonoPorId("2");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void crearTelefono_deberiaCrearTelefonoConExito() {
        when(telefonoService.guardarTelefono(telefono)).thenReturn(telefono);

        ResponseEntity<?> response = telefonoController.crearTelefono(telefono, locale);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(telefono, response.getBody());
    }

    @Test
    void crearTelefono_deberiaRetornarErrorSiNombreEsNulo() {
        telefono.setNombre(null);
        when(messageSource.getMessage("nombre.requerido", null, locale)).thenReturn("El nombre es requerido.");

        ResponseEntity<?> response = telefonoController.crearTelefono(telefono, locale);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Object body = response.getBody(); // Obtener el body como Object para evitar el casting prematuro
        assertNotNull(body, "El cuerpo de la respuesta no debe ser nulo."); // Verificar que el body no sea nulo

        if (body instanceof ErrorResponse) { // Verificar si el body es una instancia de ErrorResponse
            ErrorResponse errorResponse = (ErrorResponse) body;
            assertEquals(ErrorCode.NOMBRE_REQUERIDO.getCode(), errorResponse.getCodigoError());
        } else {
            fail("El cuerpo de la respuesta no es de tipo ErrorResponse."); // Fallar la prueba si no es ErrorResponse
        }
    }

    @Test
    void actualizarTelefono_deberiaActualizarTelefonoConExito() {
        when(telefonoService.obtenerTelefonoPorId("1")).thenReturn(Optional.of(telefono));
        when(telefonoService.guardarTelefono(telefono)).thenReturn(telefono);

        ResponseEntity<?> response = telefonoController.actualizarTelefono("1", telefono, locale);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(telefono, response.getBody());
    }

    @Test
    void actualizarTelefono_deberiaRetornarNotFoundSiNoExiste() {
        when(telefonoService.obtenerTelefonoPorId("2")).thenReturn(Optional.empty());
        when(messageSource.getMessage("telefono.no.encontrado", null, locale)).thenReturn("Telefono no encontrado");

        ResponseEntity<?> response = telefonoController.actualizarTelefono("2", telefono, locale);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Object body = response.getBody(); // Obtener el body como Object

        assertNotNull(body, "El cuerpo de la respuesta no debe ser nulo."); // Verificar que el body no sea nulo

        if (body instanceof ErrorResponse) { // Verificar si el body es una instancia de ErrorResponse
            ErrorResponse errorResponse = (ErrorResponse) body;
            assertEquals(ErrorCode.TELEFONO_NO_ENCONTRADO.getCode(), errorResponse.getCodigoError());
        } else {
            fail("El cuerpo de la respuesta no es de tipo ErrorResponse."); // Fallar la prueba si no es ErrorResponse
        }
    }

    @Test
    void eliminarTelefono_deberiaEliminarTelefonoConExito() {
        when(telefonoService.obtenerTelefonoPorId("1")).thenReturn(Optional.of(telefono));

        ResponseEntity<Void> response = telefonoController.eliminarTelefono("1");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(telefonoService, times(1)).eliminarTelefono("1");
    }

    @Test
    void eliminarTelefono_deberiaRetornarNotFoundSiNoExiste() {
        when(telefonoService.obtenerTelefonoPorId("2")).thenReturn(Optional.empty());

        ResponseEntity<Void> response = telefonoController.eliminarTelefono("2");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}