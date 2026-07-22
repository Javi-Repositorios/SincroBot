package bot.SincroStock.controladores;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class ChatControlador {

	    private ChatClient chatClient = null;
	    
	    public ChatControlador(ChatClient.Builder builder)
	    {
	        this.chatClient = builder.build();
	    }

	    
	    
	    @PostMapping
	    public String responder(@RequestBody String mensajeUsuario) {
	        return this.chatClient.prompt()
	                .user(mensajeUsuario)
	                .functions() // Habilita la herramienta definida
	                .call()
	                .content();
	    }
}

