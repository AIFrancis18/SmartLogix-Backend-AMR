package com.smartlogix.serviciopedido.service;

import com.smartlogix.serviciopedido.dto.NotificacionDTO;
import com.smartlogix.serviciopedido.producer.NotificacionProducer;
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
    private final NotificacionProducer notificacionProducer;

    public PedidoServiceImpl(PedidoRepository repository,
                            EnvioClient envioClient,
                            InventarioClient inventarioClient,
                            NotificacionProducer notificacionProducer) {

        this.repository = repository;
        this.envioClient = envioClient;
        this.inventarioClient = inventarioClient;
        this.notificacionProducer = notificacionProducer;
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

        NotificacionDTO notificacion =
                new NotificacionDTO(
                        pedidoGuardado.getCliente(),
                        "📦 Pedido #" + pedidoGuardado.getId()
                        + " creado | Producto: " + pedidoGuardado.getProducto()
                        + " | Cantidad: " + pedidoGuardado.getCantidad()
                        + " | Dirección: " + pedidoGuardado.getDireccion()
                        + " | Estado: " + pedidoGuardado.getEstado(),
                        pedidoGuardado.getId()
                );

        notificacionProducer.enviar(notificacion);

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

        Pedido actualizado = repository.save(pedido);

        // 🔔 Crear notificación
        NotificacionDTO notificacion =
                new NotificacionDTO(
                        pedido.getCliente(),
                        "🔄 Pedido #" + pedido.getId()
                        + " actualizado | Producto: "
                        + pedido.getProducto()
                        + " | Nuevo estado: "
                        + estado,
                        pedido.getId()
                );

        notificacionProducer.enviar(notificacion);

        return actualizado;
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

        // 🔥 ENVIAR NOTIFICACIÓN
        NotificacionDTO notificacion =
                new NotificacionDTO(
                        pedido.getCliente(),
                        "🗑️ Pedido #" + pedido.getId()
                        + " eliminado | Producto: "
                        + pedido.getProducto()
                        + " | Cantidad: "
                        + pedido.getCantidad(),
                        pedido.getId()
                );

        notificacionProducer.enviar(notificacion);

        repository.delete(pedido);

        System.out.println("✅ Pedido eliminado");
    }

}
