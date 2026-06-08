package com.smartlogix.serviciopedido.producer;

import com.smartlogix.serviciopedido.config.RabbitMQConfig;
import com.smartlogix.serviciopedido.dto.NotificacionDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificacionProducer {

    private final RabbitTemplate rabbitTemplate;

    public NotificacionProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void enviar(NotificacionDTO notificacion) {

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.COLA_NOTIFICACIONES,
                notificacion
        );

        System.out.println(
                "📨 Mensaje enviado a RabbitMQ: "
                        + notificacion.getMensaje()
        );
    }
}