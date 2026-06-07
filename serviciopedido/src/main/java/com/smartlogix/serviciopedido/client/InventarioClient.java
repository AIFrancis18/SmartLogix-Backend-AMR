package com.smartlogix.serviciopedido.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "servicioinventario", url = "http://servicioinventario:8084")
public interface InventarioClient {

    // 🔥 validar stock
    @GetMapping("/productos/stock")
    boolean hayStock(@RequestParam String nombre,
                     @RequestParam int cantidad);

    // 🔥 descontar stock
    @PutMapping("/productos/descontar")
    void descontarStock(@RequestParam String nombre,
                        @RequestParam int cantidad);
}