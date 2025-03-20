package com.banco.admintelefonos.enums;

public enum ErrorCode {

    NOMBRE_REQUERIDO("1001"),
    NOMBRE_CARACTERES_ESPECIALES("1002"),
    MARCA_REQUERIDO("1003"),
    MARCA_CARACTERES_ESPECIALES("1004"),
    MODELO_REQUERIDO("1005"),
    MODELO_CARACTERES_ESPECIALES("1006"),
    NOMBRE_CORTO_REQUERIDO("1007"),
    NOMBRE_CORTO_CARACTERES_ESPECIALES("1008"),
    FECHA_CREACION_REQUERIDO("1009"),
    TELEFONO_NO_ENCONTRADO("1010"),
    FECHA_CREACION_FORMATO_INVALIDO("1011"),
    IMEI_YA_EXISTE("1012"),
    IMEI_REQUERIDO("1013"),
    EMAIL_FORMATO_INVALIDO("1014");

    private final String code;

    ErrorCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
