package com.smartlogix.servicionotificacion.service;

import com.smartlogix.servicionotificacion.model.Notificacion;
import com.smartlogix.servicionotificacion.repository.NotificacionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificacionServiceImpl
        implements NotificacionService {

    private final NotificacionRepository repository;

    public NotificacionServiceImpl(
            NotificacionRepository repository) {
        this.repository = repository;
    }

    @Override
    public Notificacion crear(
            Notificacion notificacion) {

        if (notificacion.getMensaje() == null
                || notificacion.getMensaje().isEmpty()) {

            throw new RuntimeException(
                    "El mensaje es obligatorio"
            );
        }

        // Estado por defecto
        notificacion.setEstado("NO_LEIDA");

        // Fecha automática
        notificacion.setFecha(
                LocalDateTime.now()
        );

        return repository.save(notificacion);
    }

    @Override
    public List<Notificacion> listar() {
        return repository.findAll();
    }
}