package com.banco.admintelefonos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private String codigoError;
    private String mensaje;
}
