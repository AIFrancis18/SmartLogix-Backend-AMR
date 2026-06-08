package com.smartlogix.servicionotificacion.consumer;

import com.smartlogix.servicionotificacion.dto.NotificacionDTO;
import com.smartlogix.servicionotificacion.model.Notificacion;
import com.smartlogix.servicionotificacion.repository.NotificacionRepository;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class NotificacionConsumer {

    private final NotificacionRepository repository;

    public NotificacionConsumer(
            NotificacionRepository repository) {

        this.repository = repository;
    }

    @RabbitListener(queues = "cola_notificaciones")
    public void recibir(NotificacionDTO dto) {

        System.out.println("=================================");
        System.out.println("📩 NOTIFICACIÓN RECIBIDA");
        System.out.println("Cliente: " + dto.getCliente());
        System.out.println("Mensaje: " + dto.getMensaje());
        System.out.println("=================================");

        Notificacion notificacion = new Notificacion();

        notificacion.setUsuario(dto.getCliente());
        notificacion.setMensaje(dto.getMensaje());
        notificacion.setTipo("PEDIDO");
        notificacion.setEstado("NO_LEIDA");
        notificacion.setFecha(
        LocalDateTime.now(java.time.ZoneId.of("America/Santiago")));
        notificacion.setReferenciaId(dto.getReferenciaId());

        repository.save(notificacion);

        System.out.println("✅ Notificación guardada");
    }
}