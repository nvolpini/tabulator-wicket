package tabulator.wicket;

import java.io.Serializable;

import org.apache.wicket.Component;

public interface ITabulatorInitializer extends Serializable {

    /**
     * Gera o script JavaScript final para inicializar o Tabulator,
     * usando as opções mescladas.
     */
	String generateScript(Component component, String tableVarName);

}