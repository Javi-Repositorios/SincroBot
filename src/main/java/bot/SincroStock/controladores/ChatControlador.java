package bot.SincroStock.controladores;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import bot.SincroStock.tools.DateTool;
import bot.SincroStock.tools.VectorStoreTool;



@RestController
@RequestMapping("/api/chat")
public class ChatControlador {

	    private ChatClient chatClient;
	    
	    private final VectorStoreTool vectorStoreTool;
	    
	    
	    //SE LE PASA AL CONSTRUCTOR
	    //EL CHAT Y LA TOOL DE BUSQUEDA EN PGVECTOR	    
	    public ChatControlador(ChatClient.Builder builder, ChatMemory chatMemory, VectorStoreTool vectorTool){
	    	
	        // Instanciación del asesor mediante su Builder
	        MessageChatMemoryAdvisor memoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();

	        this.vectorStoreTool = vectorTool;
	        
	        String systemPrompt = """
	                Eres un asistente de gestión de inventario y ventas para la base de datos 'sincrostock'.
	                Tienes acceso a la herramienta 'buscarEnSincrostock' para consultar la base de datos vectorial.
	                
	                Las entidades del sistema son:
	                - trabajadores: Son quienes generan el pedido con fecha, asociando el cliente a sus articulos y cantidades, mediante las entidades pedido y lineas de pedido.
	                - roles: Especializan al trabajador, asignandole funciones mediante la etiqueta asociada al id: Vendedor, Director (Pueden ser ambas)
	                - clientes: Información de los compradores (nif, nombre, email y telefono).
	                - articulos: Catálogo con id, nombre, precio  y stock disponible.
	                - pedidos: Cabeceras de compra asociadas a un cliente, un trabajador y una fecha, contiene lineas de pedido de las que obtiene el importe
	                - lineas_de_pedido: Detalle de productos asociadas a cada pedido, con la cantidad y el precio.
	                
	                Si la pregunta del usuario requiere consultar información de estas entidades, usa la herramienta 'buscarEnSincrostock' pasando una consulta clara.
	                """;
	        
	        
	        
	        this.chatClient = builder
	        		.defaultSystem(systemPrompt)
	        		.defaultAdvisors(memoryAdvisor)
	        		.defaultTools(vectorStoreTool)
	        		.build();
	    }
	    
	    
	    @PostMapping
	    public String responder(@RequestBody String mensajeUsuario, @RequestParam String chatId) {
	        return this.chatClient.prompt()
	                .user(mensajeUsuario)
	                // Usamos el nombre del parámetro directamente como String
	                .advisors(a -> a.param("chat_memory_conversation_id", chatId))
	                .tools(new DateTool())  // <--Herramienta puntual
	                .call()
	                .content();
	    }
}

