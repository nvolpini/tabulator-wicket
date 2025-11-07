package tabulator.wicket;

import java.io.Serializable;

@FunctionalInterface
public interface TableFunction extends Serializable {
    String getFunctionBody(String tableVar);
}