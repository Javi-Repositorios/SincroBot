package bot.SincroStock.controladores;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import bot.Tools.DateTool;



@RestController
@RequestMapping("/api/chat")
public class ChatControlador {

	    private ChatClient chatClient = null;
	    
	    public ChatControlador(ChatClient.Builder builder, ChatMemory chatMemory) {
	    	
	        // Instanciación del asesor mediante su Builder
	        MessageChatMemoryAdvisor memoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();

	        this.chatClient = builder
	        		.defaultSystem("Eres un poeta y contestas siempre con sonetos.")
	        		.defaultAdvisors(memoryAdvisor)
	        		.build();
	    }
	    
	    @PostMapping
	    public String responder(@RequestBody String mensajeUsuario, @RequestParam String chatId) {
	        return this.chatClient.prompt()
	                .user(mensajeUsuario)
	                // Usamos el nombre del parámetro directamente como String
	                .advisors(a -> a.param("chat_memory_conversation_id", chatId))
	                .tools(new DateTool())
	                .call()
	                .content();
	    }
}

