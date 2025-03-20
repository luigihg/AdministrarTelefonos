package com.banco.admintelefonos.model;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "telefonos")
@Data
public class Telefono {

    @Id
    private String id;

    private String nombre;
    private String marca;
    private String modelo;
    private String nombreCorto;
    private LocalDateTime fechaCreacion;
    private String imei;
    private String numeroCelular;
    private String emailSoporte;
    private Boolean tieneSistemaIOS;

}
