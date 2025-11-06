package tabulator.wicket;

import java.io.IOException;

import org.apache.wicket.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class TabulatorInitializerValidator {

    private static final Logger log = LoggerFactory.getLogger(TabulatorInitializerValidator.class);
    private final ObjectMapper mapper = new ObjectMapper();

    public enum ValidationMode {
        STRICT,   // Lança exception em caso de erro
        LENIENT   // Apenas loga o erro
    }

    private final ValidationMode mode;

    public TabulatorInitializerValidator(ValidationMode mode) {
        this.mode = mode;
    }
    
    public ObjectNode validateAndExtract(Component component, ITabulatorInitializer initializer) {
        return initializer.getRawContent().map(content -> {
            try {
                return extractAndParseJson(content);
            } catch (Exception e) {
                String msg = "Erro parseando JSON do initializer " + initializer.getClass().getSimpleName();
                if (mode == ValidationMode.STRICT)
                    throw new TabulatorWicketException(msg, e);
                log.warn("{} — ignorando erro: {}", msg, e.getMessage());
                return mapper.createObjectNode();
            }
        }).orElse(mapper.createObjectNode());
    }

    private ObjectNode extractAndParseJson(String rawJs) throws IOException {
        int open = rawJs.indexOf('{');
        int close = rawJs.lastIndexOf('}');

        if (open < 0 || close < 0 || close <= open) {
            // Nenhum JSON detectado — retorna objeto vazio
            return mapper.createObjectNode();
        }

        String jsonText = rawJs.substring(open, close + 1);

        // --- SANITIZAÇÃO ---
        String sanitized = jsonText
            // Remove comentários JS
            .replaceAll("(?m)//.*?$", "")
            // Remove vírgulas antes de chaves ou colchetes de fechamento
            .replaceAll(",\\s*([}\\]])", "$1")
            // Adiciona aspas em chaves não-entre-aspas (layout: → "layout":)
            .replaceAll("(?m)(\\s*)([a-zA-Z0-9_]+)\\s*:", "$1\"$2\":")
            // Remove linhas vazias
            .replaceAll("(?m)^\\s*$", "")
            // Remove múltiplos espaços
            .replaceAll("\\s{2,}", " ");

        // Tenta converter em JSON
        try {
            return (ObjectNode) mapper.readTree(sanitized);
        } catch (Exception e) {
            throw new IOException("Erro convertendo objeto JS para JSON: " + e.getMessage() + "\nTexto sanitizado:\n" + sanitized, e);

        }
    }

}
