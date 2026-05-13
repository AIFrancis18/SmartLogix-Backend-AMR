package com.smartlogix.servicioenvio.controller;

import com.smartlogix.servicioenvio.model.Envio;
import com.smartlogix.servicioenvio.service.EnvioService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/envios")
public class EnvioController {

    private final EnvioService service;

    public EnvioController(EnvioService service) {
        this.service = service;
    }

    
    @PostMapping
    public Envio crear(@RequestBody Envio envio){
        return service.crear(envio);
    }

    
    @GetMapping
    public List<Envio> listar(){
        return service.listar();
    }

    
    @GetMapping("/pedido/{pedidoId}")
    public List<Envio> porPedido(@PathVariable Long pedidoId){
        return service.buscarPorPedido(pedidoId);
    }

    
    @PutMapping("/{id}/estado")
    public Envio actualizarEstado(@PathVariable Long id,
                                  @RequestParam String estado){

        return service.actualizarEstado(id, estado);
    }

    
    @DeleteMapping("/pedido/{pedidoId}")
    public void eliminarPorPedido(@PathVariable Long pedidoId){
        service.eliminarPorPedido(pedidoId);
    }
}