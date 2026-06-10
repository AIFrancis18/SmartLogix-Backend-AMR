package com.smartlogix.servicionotificacion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionDTO {

    private String cliente;
    private String mensaje;
    private Long referenciaId;
}