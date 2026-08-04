package bot.SincroStock.servicios;

import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ServicioEtl {

    private final JdbcTemplate jdbcTemplate;
    private final VectorStore vectorStore;

    public ServicioEtl(JdbcTemplate jdbcTemplate, VectorStore vectorStore) {
        this.jdbcTemplate = jdbcTemplate;
        this.vectorStore = vectorStore;
    }

    @Transactional(readOnly = false)
    public void ejecutarPipelineEtl() {
        List<Document> documentos = new ArrayList<>();

        documentos.addAll(extraerClientes());
        documentos.addAll(extraerArticulos());
        documentos.addAll(extraerTrabajadores());
        documentos.addAll(extraerPedidos());
        documentos.addAll(extraerLineasPedido());

        if (!documentos.isEmpty()) {
            this.vectorStore.accept(documentos);
        }
    }

    // 1. Entidad: Cliente (Tabla: clientes)
    private List<Document> extraerClientes() {
        String sql = "SELECT nif, nombre, email, telefono FROM clientes";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            String nif = rs.getString("nif");
            String nombre = obtenerCadenaSegura(rs, "nombre");
            String email = obtenerCadenaSegura(rs, "email");
            String telefono = obtenerCadenaSegura(rs, "telefono");

            String contenido = String.format(
                "Cliente NIF: %s | Nombre: %s | Email: %s | Teléfono: %s",
                nif, nombre, email, telefono
            );

            Map<String, Object> metadata = Map.of(
                "entidad", "clientes",
                "id_registro", nif
            );

            String docIdUuid = UUID.nameUUIDFromBytes(("cliente_" + nif).getBytes(StandardCharsets.UTF_8)).toString();

            return Document.builder()
                    .id(docIdUuid)
                    .text(contenido)
                    .metadata(metadata)
                    .build();
        });
    }

    // 2. Entidad: Articulo (Tabla: articulos, PK: articulo_id)
    private List<Document> extraerArticulos() {
        String sql = "SELECT articulo_id, nombre, precio, stock_disponible FROM articulos";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            int idArticulo = rs.getInt("articulo_id");
            String nombre = obtenerCadenaSegura(rs, "nombre");
            float precio = rs.getFloat("precio");
            int stockDisponible = rs.getInt("stock_disponible");

            String contenido = String.format(
                "Artículo ID: %d | Nombre: %s | Precio: %.2f EUR | Stock disponible: %d unidades",
                idArticulo, nombre, precio, stockDisponible
            );

            Map<String, Object> metadata = Map.of(
                "entidad", "articulos",
                "id_registro", String.valueOf(idArticulo)
            );

            String docIdUuid = UUID.nameUUIDFromBytes(("articulo_" + idArticulo).getBytes(StandardCharsets.UTF_8)).toString();

            return Document.builder()
                    .id(docIdUuid)
                    .text(contenido)
                    .metadata(metadata)
                    .build();
        });
    }

    // 3. Entidad: Trabajador (Tabla: trabajadores, FK: ref_user, ref_rol)
    private List<Document> extraerTrabajadores() {
        String sql = """
            SELECT 
                t.username, 
                t.nombre, 
                t.apellidos, 
                STRING_AGG(r.rol, ', ') AS roles
            FROM trabajadores t
            LEFT JOIN roles_de_trabajadores rt ON t.username = rt.ref_user
            LEFT JOIN roles r ON rt.ref_rol = r.rol_id
            GROUP BY t.username, t.nombre, t.apellidos
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            String username = rs.getString("username");
            String nombre = obtenerCadenaSegura(rs, "nombre");
            String apellidos = obtenerCadenaSegura(rs, "apellidos");
            String roles = obtenerCadenaSegura(rs, "roles");

            String contenido = String.format(
                "Trabajador Username: %s | Nombre: %s %s | Roles: %s",
                username, nombre, apellidos, roles
            );

            Map<String, Object> metadata = Map.of(
                "entidad", "trabajadores",
                "id_registro", username
            );

            String docIdUuid = UUID.nameUUIDFromBytes(("trabajador_" + username).getBytes(StandardCharsets.UTF_8)).toString();

            return Document.builder()
                    .id(docIdUuid)
                    .text(contenido)
                    .metadata(metadata)
                    .build();
        });
    }

    // 4. Entidad: Pedido (Tabla: pedidos, Columns: fecha_pedido, ref_cliente, ref_usuario)
    private List<Document> extraerPedidos() {
        String sql = """
            SELECT 
                p.id, 
                p.total,
                p.fecha_pedido, 
                c.nombre AS nombre_cliente, 
                t.username AS username_vendedor
            FROM pedidos p
            LEFT JOIN clientes c ON p.ref_cliente = c.nif
            LEFT JOIN trabajadores t ON p.ref_usuario = t.username
            ORDER BY p.id
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            long id = rs.getLong("id");
            double total = rs.getDouble("total");
            String fecha = rs.getTimestamp("fecha_pedido") != null ? rs.getTimestamp("fecha_pedido").toString() : "N/A";
            String cliente = obtenerCadenaSegura(rs, "nombre_cliente");
            String vendedor = obtenerCadenaSegura(rs, "username_vendedor");

            String contenido = String.format(
                "Pedido ID: %d | Fecha: %s | Total: %.2f EUR | Cliente: %s | Vendedor: %s",
                id, fecha, total, cliente, vendedor
            );

            Map<String, Object> metadata = Map.of(
                "entidad", "pedidos",
                "id_registro", String.valueOf(id)
            );

            String docIdUuid = UUID.nameUUIDFromBytes(("pedido_" + id).getBytes(StandardCharsets.UTF_8)).toString();

            return Document.builder()
                    .id(docIdUuid)
                    .text(contenido)
                    .metadata(metadata)
                    .build();
        });
    }

    // 5. Entidad: LineasPedido (Tabla: lineas_de_pedido, PK: id_linea, FK: articulo_id, pedido_id)
    private List<Document> extraerLineasPedido() {
        String sql = """
            SELECT 
                lp.id_linea,
                lp.pedido_id,
                lp.articulo_id,
                a.nombre AS nombre_articulo,
                lp.cantidad,
                lp.precio_venta
            FROM lineas_de_pedido lp
            LEFT JOIN articulos a ON lp.articulo_id = a.articulo_id
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            int idLinea = rs.getInt("id_linea");
            long pedidoId = rs.getLong("pedido_id");
            int articuloId = rs.getInt("articulo_id");
            String nombreArticulo = obtenerCadenaSegura(rs, "nombre_articulo");
            int cantidad = rs.getInt("cantidad");
            float precioVenta = rs.getFloat("precio_venta");

            String contenido = String.format(
                "Línea de Pedido ID: %d | Pedido ID: %d | Artículo ID: %d | Nombre Artículo: %s | Cantidad: %d | Precio Venta: %.2f EUR",
                idLinea, pedidoId, articuloId, nombreArticulo, cantidad, precioVenta
            );

            Map<String, Object> metadata = Map.of(
                "entidad", "lineas_de_pedido",
                "id_registro", String.valueOf(idLinea),
                "pedido_id", String.valueOf(pedidoId),
                "articulo_id", String.valueOf(articuloId)
            );

            String docIdUuid = UUID.nameUUIDFromBytes(("lineapedido_" + idLinea).getBytes(StandardCharsets.UTF_8)).toString();

            return Document.builder()
                    .id(docIdUuid)
                    .text(contenido)
                    .metadata(metadata)
                    .build();
        });
    }

    private String obtenerCadenaSegura(ResultSet rs, String columna) throws SQLException {
        String valor = rs.getString(columna);
        return valor != null ? valor : "N/A";
    }
}