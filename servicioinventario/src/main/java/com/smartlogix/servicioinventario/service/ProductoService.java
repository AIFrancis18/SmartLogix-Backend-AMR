package com.smartlogix.servicioinventario.service;

import com.smartlogix.servicioinventario.model.Producto;

import java.util.List;

public interface ProductoService {

    Producto crear(Producto producto);

    List<Producto> listar();

    boolean hayStock(String nombre, int cantidad);

    void descontarStock(String nombre, int cantidad);

    Producto buscarPorId(Long id);

    Producto actualizar(Producto producto);

    void eliminar(Long id);
}