package com.banco.admintelefonos.controller;

import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.banco.admintelefonos.dto.ErrorResponse;
import com.banco.admintelefonos.enums.ErrorCode;
import com.banco.admintelefonos.model.Telefono;
import com.banco.admintelefonos.service.TelefonoService;

import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/telefonos")
public class TelefonoController {

    private final TelefonoService telefonoService;
    private final MessageSource messageSource;

    public TelefonoController(TelefonoService telefonoService, MessageSource messageSource) {
        this.telefonoService = telefonoService;
        this.messageSource = messageSource;
    }

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
    public ResponseEntity<?> crearTelefono(@RequestBody Telefono telefono, Locale locale) {
        ResponseEntity<?> validacion = validarTelefono(telefono, locale, true);
        if (validacion != null) {
            return validacion;
        }

        Telefono nuevoTelefono = telefonoService.guardarTelefono(telefono);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoTelefono);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarTelefono(@PathVariable String id, @RequestBody Telefono telefono,
            Locale locale) {
        ResponseEntity<?> validacion = validarTelefono(telefono, locale, false);
        if (validacion != null) {
            return validacion;
        }

        Optional<Telefono> telefonoExistenteOptional = telefonoService.obtenerTelefonoPorId(id);
        if (telefonoExistenteOptional.isPresent()) {
            Telefono telefonoExistente = telefonoExistenteOptional.get();

            // Actualizar los campos que se proporcionan en el objeto telefono
            if (telefono.getNombre() != null) {
                telefonoExistente.setNombre(telefono.getNombre());
            }
            if (telefono.getMarca() != null) {
                telefonoExistente.setMarca(telefono.getMarca());
            }
            if (telefono.getModelo() != null) {
                telefonoExistente.setModelo(telefono.getModelo());
            }
            if (telefono.getNombreCorto() != null) {
                telefonoExistente.setNombreCorto(telefono.getNombreCorto());
            }
            if (telefono.getFechaCreacion() != null) {
                telefonoExistente.setFechaCreacion(telefono.getFechaCreacion());
            }
            if (telefono.getNumeroCelular() != null) {
                telefonoExistente.setNumeroCelular(telefono.getNumeroCelular());
            }
            if (telefono.getEmailSoporte() != null) {
                telefonoExistente.setEmailSoporte(telefono.getEmailSoporte());
            }
            if (telefono.getTieneSistemaIOS() != null) {
                telefonoExistente.setTieneSistemaIOS(telefono.getTieneSistemaIOS());
            }

            Telefono telefonoActualizado = telefonoService.guardarTelefono(telefonoExistente);
            return ResponseEntity.ok(telefonoActualizado);
        } else {
            return crearErrorResponse(ErrorCode.TELEFONO_NO_ENCONTRADO, "telefono.no.encontrado", locale);
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

    private ResponseEntity<?> validarTelefono(Telefono telefono, Locale locale, boolean esCreacion) {
        String regex = "[a-zA-Z0-9\\s]+";
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"; // Expresión regular para email

        if (telefono.getNombre() == null || telefono.getNombre().trim().isEmpty()) {
            return crearErrorResponse(ErrorCode.NOMBRE_REQUERIDO, "nombre.requerido", locale);
        }
        if (!Pattern.matches(regex, telefono.getNombre())) {
            return crearErrorResponse(ErrorCode.NOMBRE_CARACTERES_ESPECIALES, "nombre.caracteres.especiales", locale);
        }

        if (telefono.getMarca() == null || telefono.getMarca().trim().isEmpty()) {
            return crearErrorResponse(ErrorCode.MARCA_REQUERIDO, "marca.requerido", locale);
        }
        if (!Pattern.matches(regex, telefono.getMarca())) {
            return crearErrorResponse(ErrorCode.MARCA_CARACTERES_ESPECIALES, "marca.caracteres.especiales", locale);
        }

        if (telefono.getModelo() == null || telefono.getModelo().trim().isEmpty()) {
            return crearErrorResponse(ErrorCode.MODELO_REQUERIDO, "modelo.requerido", locale);
        }
        if (!Pattern.matches(regex, telefono.getModelo())) {
            return crearErrorResponse(ErrorCode.MODELO_CARACTERES_ESPECIALES, "modelo.caracteres.especiales", locale);
        }

        if (telefono.getNombreCorto() == null || telefono.getNombreCorto().trim().isEmpty()) {
            return crearErrorResponse(ErrorCode.NOMBRE_CORTO_REQUERIDO, "nombreCorto.requerido", locale);
        }
        if (!Pattern.matches(regex, telefono.getNombreCorto())) {
            return crearErrorResponse(ErrorCode.NOMBRE_CORTO_CARACTERES_ESPECIALES, "nombreCorto.caracteres.especiales",
                    locale);
        }

        if (telefono.getFechaCreacion() == null) {
            return crearErrorResponse(ErrorCode.FECHA_CREACION_REQUERIDO, "fechaCreacion.requerido", locale);
        }

        if (esCreacion) {
            if (telefono.getImei() != null && !telefono.getImei().trim().isEmpty()) {
                Optional<Telefono> telefonoExistenteImei = telefonoService.obtenerTelefonoPorImei(telefono.getImei());
                if (telefonoExistenteImei.isPresent()) {
                    return crearErrorResponse(ErrorCode.IMEI_YA_EXISTE, "imei.ya.existe", locale);
                }
            } else {
                return crearErrorResponse(ErrorCode.IMEI_REQUERIDO, "imei.requerido", locale);
            }
        }

        if (telefono.getEmailSoporte() != null && !telefono.getEmailSoporte().trim().isEmpty()) {
            if (!Pattern.matches(emailRegex, telefono.getEmailSoporte())) {
                return crearErrorResponse(ErrorCode.EMAIL_FORMATO_INVALIDO, "email.formato.invalido", locale);
            }
        }

        return null;
    }

    private ResponseEntity<ErrorResponse> crearErrorResponse(ErrorCode codigoError, String mensajeKey, Locale locale) {
        String mensaje = messageSource.getMessage(mensajeKey, null, locale);
        ErrorResponse errorResponse = new ErrorResponse(codigoError.getCode(), mensaje);
        return ResponseEntity.badRequest().body(errorResponse);
    }

}