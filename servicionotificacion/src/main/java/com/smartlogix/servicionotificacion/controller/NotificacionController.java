package com.smartlogix.servicionotificacion.controller;

import com.smartlogix.servicionotificacion.model.Notificacion;
import com.smartlogix.servicionotificacion.service.NotificacionService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notificaciones")
@CrossOrigin("*")
public class NotificacionController {

    private final NotificacionService service;

    public NotificacionController(
            NotificacionService service) {

        this.service = service;
    }

    @PostMapping
    public Notificacion crear(
            @RequestBody Notificacion notificacion) {

        return service.crear(notificacion);
    }

    @GetMapping
    public List<Notificacion> listar() {

        return service.listar();
    }
}