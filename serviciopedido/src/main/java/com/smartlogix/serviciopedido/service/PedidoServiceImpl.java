package com.smartlogix.serviciopedido.service;

import com.smartlogix.serviciopedido.client.EnvioClient;
import com.smartlogix.serviciopedido.client.InventarioClient;
import com.smartlogix.serviciopedido.model.Pedido;
import com.smartlogix.serviciopedido.repository.PedidoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository repository;
    private final EnvioClient envioClient;
    private final InventarioClient inventarioClient;

    public PedidoServiceImpl(PedidoRepository repository,
                             EnvioClient envioClient,
                             InventarioClient inventarioClient) {
        this.repository = repository;
        this.envioClient = envioClient;
        this.inventarioClient = inventarioClient;
    }

    @Override
    public Pedido guardar(Pedido pedido) {

        // 🔥 VALIDACIONES
        if (pedido.getProducto() == null || pedido.getProducto().isEmpty()) {
            throw new RuntimeException("El producto es obligatorio");
        }

        if (pedido.getCantidad() <= 0) {
            throw new RuntimeException("La cantidad debe ser mayor a 0");
        }

        if (pedido.getDireccion() == null || pedido.getDireccion().isEmpty()) {
            throw new RuntimeException("La dirección es obligatoria");
        }

        if (pedido.getCliente() == null || pedido.getCliente().isEmpty()) {
            throw new RuntimeException("El cliente es obligatorio");
        }

        // 🔥 VALIDAR STOCK EN INVENTARIO
        boolean hayStock = inventarioClient.hayStock(
                pedido.getProducto(),
                pedido.getCantidad()
        );

        if (!hayStock) {
            throw new RuntimeException(
                    "Stock insuficiente para el producto: "
                            + pedido.getProducto()
            );
        }

        // 🔥 DESCONTAR STOCK
        inventarioClient.descontarStock(
                pedido.getProducto(),
                pedido.getCantidad()
        );

        System.out.println("✅ Stock descontado correctamente");

        // 🔥 ESTADO INICIAL
        pedido.setEstado("PENDIENTE");

        // 🔥 GUARDAR PEDIDO
        Pedido pedidoGuardado = repository.save(pedido);

        // 🔥 CREAR ENVÍO AUTOMÁTICO
        Map<String, Object> envio = Map.of(
                "pedidoId", pedidoGuardado.getId(),
                "direccion", pedidoGuardado.getDireccion(),
                "estado", "PENDIENTE"
        );

        try {

            envioClient.crearEnvio(envio);

            System.out.println("✅ Envío creado automáticamente");

        } catch (Exception e) {

            System.out.println("❌ Error al crear envío");
            System.out.println(e.getMessage());
        }

        return pedidoGuardado;
    }

    @Override
    public List<Pedido> listar() {
        return repository.findAll();
    }

    @Override
    public Pedido actualizarEstado(Long id, String estado) {

        Pedido pedido = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        pedido.setEstado(estado);

        return repository.save(pedido);
    }

    @Override
    public List<Pedido> buscarPorCliente(String cliente) {
        return repository.findByCliente(cliente);
    }

    @Override
    public Map<String, Object> obtenerPedidoConEnvios(Long id) {

        Pedido pedido = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        List<Object> envios = envioClient.obtenerEnvios(id);

        return Map.of(
                "pedido", pedido,
                "envios", envios
        );
    }

    // 🔥 ELIMINAR PEDIDO + ENVÍOS
    @Override
    public void eliminar(Long id) {

        Pedido pedido = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        try {

            envioClient.eliminarEnviosPorPedido(id);

            System.out.println("✅ Envíos eliminados");

        } catch (Exception e) {

            System.out.println("❌ Error eliminando envíos");
            System.out.println(e.getMessage());
        }

        repository.delete(pedido);

        System.out.println("✅ Pedido eliminado");
    }
}