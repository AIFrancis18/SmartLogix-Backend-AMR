package com.smartlogix.serviciopedido.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

@Configuration
public class RabbitMQConfig {

    public static final String COLA_NOTIFICACIONES =
            "cola_notificaciones";

    @Bean
    public Queue colaNotificaciones() {
        return new Queue(
                COLA_NOTIFICACIONES,
                true
        );
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}