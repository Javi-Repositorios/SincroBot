package bot.SincroStock.tools;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.ai.document.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;



@Component
public class VectorStoreTool {

    private final VectorStore vectorStore;

    public VectorStoreTool(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public record SearchQuery(
            @ToolParam(description = "Texto semántico de búsqueda relacionado con clientes, pedidos, artículos o líneas de pedido") 
            String query, 
            
            @ToolParam(description = "Cantidad máxima de registros a recuperar. Por defecto 5.") 
            int topK
        ) {}

        public record RecordDto(String id, String contenido, Object metadata) {}

        @Tool(
            name = "buscarEnSincrostock",
            description = "Consulta la base de datos sincrostock en PGVector. Permite buscar información relevante sobre clientes, pedidos, artículos y líneas de pedido."
        )
        public List<RecordDto> buscarEnSincrostock(SearchQuery request) {
            int limit = request.topK() > 0 ? request.topK() : 5;

            List<Document> results = this.vectorStore.similaritySearch(
                SearchRequest.builder()
                    .query(request.query())
                    .topK(limit)
                    .build()
            );

            return results.stream()
                .map(doc -> new RecordDto(doc.getId(), doc.getText(), doc.getMetadata()))
                .collect(Collectors.toList());
        }
}