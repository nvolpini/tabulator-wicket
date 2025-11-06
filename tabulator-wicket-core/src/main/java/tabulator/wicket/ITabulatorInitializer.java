package tabulator.wicket;

import java.io.Serializable;
import java.util.Optional;

import org.apache.wicket.Component;

public interface ITabulatorInitializer extends Serializable {

    /**
     * Gera o script JavaScript final para inicializar o Tabulator,
     * usando as opções mescladas.
     */
	String generateScript(Component component, String tableVarName);


    /**
     * Retorna o conteúdo base (template, JS puro, etc.), 
     * usado opcionalmente para validação.
     */
    default Optional<String> getRawContent() {
        return Optional.empty();
    }
}