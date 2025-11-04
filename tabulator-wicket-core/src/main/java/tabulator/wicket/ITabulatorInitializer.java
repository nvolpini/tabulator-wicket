package tabulator.wicket;

import java.io.Serializable;

import org.apache.wicket.Component;

public interface ITabulatorInitializer extends Serializable {

	String generateScript(Component component, String tableVarName);

}