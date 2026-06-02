package com.smartlogix.servicioinventario.controller;

import com.smartlogix.servicioinventario.model.Producto;
import com.smartlogix.servicioinventario.service.ProductoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }

    // 🔥 CREAR PRODUCTO
    @PostMapping
    public Producto crear(@RequestBody Producto producto) {
        return service.crear(producto);
    }

    // 🔥 LISTAR PRODUCTOS
    @GetMapping
    public List<Producto> listar() {
        return service.listar();
    }

    // 🔥 BUSCAR PRODUCTO POR ID
    @GetMapping("/{id}")
    public Producto buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    // 🔥 ACTUALIZAR PRODUCTO
    @PutMapping("/{id}")
    public Producto actualizar(@PathVariable Long id,
                               @RequestBody Producto producto) {

        producto.setId(id);

        return service.actualizar(producto);
    }

    // 🔥 ELIMINAR PRODUCTO
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }

    // 🔥 VALIDAR STOCK
    @GetMapping("/stock")
    public boolean hayStock(@RequestParam String nombre,
                            @RequestParam int cantidad) {

        return service.hayStock(nombre, cantidad);
    }

    // 🔥 DESCONTAR STOCK
    @PutMapping("/descontar")
    public void descontar(@RequestParam String nombre,
                          @RequestParam int cantidad) {

        service.descontarStock(nombre, cantidad);
    }
}