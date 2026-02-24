package tabulator.grid.wicket;

import org.apache.wicket.Component;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import tabulator.wicket.TabulatorBehavior;
import tabulator.wicket.resources.TabulatorJsonReference;

public class TabulatorJsonBehavior extends TabulatorBehavior {

    private final GridConfig gridConfig;
    private final ObjectMapper mapper = new ObjectMapper();

    public TabulatorJsonBehavior(GridConfig config) {
        super(null); // não usamos initializer
        this.gridConfig = config;
    }

    @Override
    protected final String buildTableInitializationScript(Component c) {

        try {

            String tableVar = getTableVarName();
            
            ObjectNode configNode = mapper.createObjectNode();

            // Infra básica
            configNode.put("elementId", c.getMarkupId());
            

            if (gridConfig.getAjaxUrl() != null) {
                configNode.put("ajaxUrl", gridConfig.getAjaxUrl());
            }

            if (gridConfig.getPersistenceId() != null) {
                configNode.put("persistenceId",
                        gridConfig.getPersistenceId());
            }
            

            // Columns
            configNode.set("columns",
                    mapper.valueToTree(gridConfig.getColumns()));
            

            // Options Java (defaultOptions + locale + etc)
            ObjectNode javaOptions = getFinalOptions();

            // Mesclar opções declarativas do GridConfig
            ObjectNode declarativeOptions =
                    mapper.valueToTree(gridConfig.getOptions());

            ObjectNode mergedJavaOptions =
                    mergeOptions(javaOptions, declarativeOptions);

            configNode.set("options", mergedJavaOptions);

            String json = mapper.writeValueAsString(configNode);

            return "var " + tableVar
                    + " = window.tabulator.wicket.grid.create("
                    + json
                    + ");";
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    protected final void renderHeadResources(Component c, IHeaderResponse r) {
    	r.render(JavaScriptHeaderItem.forReference(TabulatorJsonReference.get()));
    	super.renderHeadResources(c, r);
    }
    
}
