package com.smartlogix.servicionotificacion.service;

import com.smartlogix.servicionotificacion.model.Notificacion;

import java.util.List;

public interface NotificacionService {

    Notificacion crear(Notificacion notificacion);

    List<Notificacion> listar();
}