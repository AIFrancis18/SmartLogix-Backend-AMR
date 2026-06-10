package com.smartlogix.servicionotificacion.repository;

import com.smartlogix.servicionotificacion.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificacionRepository
        extends JpaRepository<Notificacion, Long> {
}