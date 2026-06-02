package com.smartlogix.servicioinventario.service;

import com.smartlogix.servicioinventario.model.Producto;
import com.smartlogix.servicioinventario.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository repository;

    public ProductoServiceImpl(ProductoRepository repository) {
        this.repository = repository;
    }

    // 🔥 CREAR PRODUCTO
    @Override
    public Producto crear(Producto producto) {

        if (producto.getNombre() == null ||
                producto.getNombre().trim().isEmpty()) {
            throw new RuntimeException("El nombre es obligatorio");
        }

        if (producto.getCategoria() == null ||
                producto.getCategoria().trim().isEmpty()) {
            throw new RuntimeException("La categoría es obligatoria");
        }

        if (producto.getStock() < 0) {
            throw new RuntimeException("El stock no puede ser negativo");
        }

        if (producto.getPrecio() <= 0) {
            throw new RuntimeException("El precio debe ser mayor a 0");
        }

        return repository.save(producto);
    }

    // 🔥 LISTAR PRODUCTOS
    @Override
    public List<Producto> listar() {
        return repository.findAll();
    }

    // 🔥 BUSCAR POR ID
    @Override
    public Producto buscarPorId(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Producto no encontrado"));
    }

    // 🔥 ACTUALIZAR PRODUCTO
    @Override
    public Producto actualizar(Producto producto) {

        Producto existente = repository.findById(producto.getId())
                .orElseThrow(() ->
                        new RuntimeException("Producto no encontrado"));

        if (producto.getNombre() == null ||
                producto.getNombre().trim().isEmpty()) {
            throw new RuntimeException("El nombre es obligatorio");
        }

        if (producto.getCategoria() == null ||
                producto.getCategoria().trim().isEmpty()) {
            throw new RuntimeException("La categoría es obligatoria");
        }

        if (producto.getStock() < 0) {
            throw new RuntimeException("El stock no puede ser negativo");
        }

        if (producto.getPrecio() <= 0) {
            throw new RuntimeException("El precio debe ser mayor a 0");
        }

        existente.setNombre(producto.getNombre());
        existente.setCategoria(producto.getCategoria());
        existente.setDescripcion(producto.getDescripcion());
        existente.setStock(producto.getStock());
        existente.setPrecio(producto.getPrecio());

        return repository.save(existente);
    }

    // 🔥 ELIMINAR PRODUCTO
    @Override
    public void eliminar(Long id) {

        if (!repository.existsById(id)) {
            throw new RuntimeException("Producto no encontrado");
        }

        repository.deleteById(id);
    }

    // 🔥 VALIDAR STOCK
    @Override
    public boolean hayStock(String nombre, int cantidad) {

        Producto producto = repository.findByNombre(nombre)
                .orElseThrow(() ->
                        new RuntimeException("Producto no encontrado"));

        return producto.getStock() >= cantidad;
    }

    // 🔥 DESCONTAR STOCK
    @Override
    public void descontarStock(String nombre, int cantidad) {

        Producto producto = repository.findByNombre(nombre)
                .orElseThrow(() ->
                        new RuntimeException("Producto no encontrado"));

        if (producto.getStock() < cantidad) {
            throw new RuntimeException("Stock insuficiente");
        }

        producto.setStock(producto.getStock() - cantidad);

        repository.save(producto);
    }
}