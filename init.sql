--- 1. Tabla: ROLES  

CREATE TABLE ROLES ( rol_id SERIAL PRIMARY KEY, rol VARCHAR(30) NOT NULL UNIQUE );  

--- 2. Tabla: TRABAJADORES 

CREATE TABLE TRABAJADORES ( username VARCHAR(20) PRIMARY KEY, password VARCHAR(255) NOT NULL,  nombre VARCHAR(50) NOT NULL, apellidos VARCHAR(50) NOT NULL );  

--- 3. Tabla: ROLES_DE_TRABAJADORES  

CREATE TABLE ROLES_DE_TRABAJADORES ( ref_rol INT, ref_user VARCHAR(20), PRIMARY KEY (ref_rol, ref_user), FOREIGN KEY (ref_rol) REFERENCES ROLES(rol_id) ON DELETE CASCADE, FOREIGN KEY (ref_user) REFERENCES TRABAJADORES(username) ON DELETE CASCADE ); 

 --- 4. Tabla: CLIENTES 

CREATE TABLE CLIENTES ( nif CHAR(9) PRIMARY KEY, nombre VARCHAR(100) NOT NULL, email VARCHAR(100) NOT NULL UNIQUE, telefono VARCHAR(15) NOT NULL, CONSTRAINT check_nif_format CHECK (nif ~ '^[0-9]{8}[A-Z]$') );  

--- 5. Tabla: ARTICULOS 

CREATE TABLE ARTICULOS ( articulo_id SERIAL PRIMARY KEY, nombre VARCHAR(100) NOT NULL, precio DECIMAL(10,2) NOT NULL CHECK (precio > 0), stock_disponible INT DEFAULT 0 CHECK (stock_disponible >= 0) );  

--- 6. Tabla: PEDIDOS  

CREATE TABLE PEDIDOS ( pedido_id SERIAL PRIMARY KEY, importe_total DECIMAL(10,2) NOT NULL DEFAULT 0, fecha_pedido TIMESTAMP DEFAULT CURRENT_TIMESTAMP, ref_usuario VARCHAR(20) NOT NULL, ref_cliente CHAR(9) NOT NULL, FOREIGN KEY (ref_usuario) REFERENCES TRABAJADORES(username), FOREIGN KEY (ref_cliente) REFERENCES CLIENTES(nif) );  

--- 7. Tabla: LINEAS_DE_PEDIDO (PK Compuesta) 

 CREATE TABLE LINEAS_DE_PEDIDa ( ref_pedido INT, linea_id INT, cantidad INT NOT NULL CHECK (cantidad > 0), precio_venta DECIMAL(10,2) NOT NULL, ref_articulo INT NOT NULL, PRIMARY KEY (ref_pedido, linea_id), FOREIGN KEY (ref_pedido) REFERENCES PEDIDOS(pedido_id) ON DELETE CASCADE, FOREIGN KEY (ref_articulo) REFERENCES ARTICULOS(articulo_id) );





-- 1. Habilitar la extensión pgvector
CREATE EXTENSION IF NOT EXISTS vector;

-- 2. Tabla para Vector Store (Spring AI)
CREATE TABLE IF NOT EXISTS vector_store (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    content TEXT,
    metadata JSONB,
    embedding VECTOR(1536)
);

CREATE INDEX IF NOT EXISTS vector_store_embedding_hnsw_idx 
ON vector_store 
USING hnsw (embedding vector_cosine_ops);







-- 3. Tabla para Chat Memory (Spring AI)
CREATE TABLE IF NOT EXISTS spring_ai_chat_memory (
    conversation_id VARCHAR(255) NOT NULL,
    sequence_id INT NOT NULL,
    type VARCHAR(50) NOT NULL,
    content TEXT NOT NULL,
    timestamp TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    PRIMARY KEY (conversation_id, sequence_id)
);

CREATE INDEX IF NOT EXISTS idx_chat_memory_conversation 
ON spring_ai_chat_memory (conversation_id);

-- 4. Inserciones de datos iniciales
INSERT INTO ROLES (rol) VALUES ('DIRECTOR'), ('VENDEDOR');  

INSERT INTO TRABAJADORES (username, password, nombre, apellidos) VALUES ('javierms', '$2a$12$k1xIcm8JfFG8oqx23M/nEOASKEWVDiZlaZJUExTHDRi71jcJLZ6tC', 'Javier', 'Martinez'); 

INSERT INTO TRABAJADORES (username, password, nombre, apellidos) VALUES ('ignacioms', '$2a$12$k1xIcm8JfFG8oqx23M/nEOASKEWVDiZlaZJUExTHDRi71jcJLZ6tC', 'Ignacio', 'Martinez'); 

 
INSERT INTO ROLES_DE_TRABAJADORES (ref_rol, ref_user) VALUES (1, 'javierms'); 

INSERT INTO ROLES_DE_TRABAJADORES (ref_rol, ref_user) VALUES (2, 'ignacioms'); 

INSERT INTO CLIENTES (nif, nombre, email, telefono) VALUES ('12345678Z', 'Cliente de Prueba', 'prueba@sincrostock.com', '666111222'); 

INSERT INTO ARTICULOS (nombre, precio, stock_disponible) VALUES ('Teclado', 25.50, 50), ('Raton', 15.00, 100);

-- 1. Inserción de la cabecera del pedido
INSERT INTO PEDIDOS (total, fecha_pedido, ref_usuario, ref_cliente)
VALUES (25.50, '2026-05-02 11:00:00', 'ignacioms', '12345678Z');

-- 2. Inserción del detalle en la línea de pedido
INSERT INTO LINEAS_DE_PEDIDO (pedido_id, id_linea, cantidad, precio_venta, articulo_id)
VALUES (
    (SELECT id FROM PEDIDOS WHERE ref_cliente = '12345678Z' ORDER BY fecha_pedido DESC LIMIT 1),
    1,
    1,
    25.50,
    (SELECT articulo_id FROM ARTICULOS WHERE nombre = 'Teclado' LIMIT 1)
);