package tabulator.wicket;

@FunctionalInterface
public interface TableFunction {
    String getFunctionBody(String tableVar);
}