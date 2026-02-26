package tabulator.grid.wicket;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.protocol.http.WebApplication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;

import tabulator.wicket.TableFunction;
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

            if (WebApplication.get().getConfigurationType() == RuntimeConfigurationType.DEVELOPMENT) {
            	mapper.enable(SerializationFeature.INDENT_OUTPUT);
            }
            
            ObjectNode configNode = mapper.createObjectNode();
            
            // Infra básica
            configNode.put("elementId", c.getMarkupId());
            

            if (gridConfig.getAjaxUrl() != null) {
                configNode.put("ajaxUrl", gridConfig.getAjaxUrl());
            }

            if (gridConfig.getPersistenceId() != null) {
                configNode.put("persistenceID",
                        gridConfig.getPersistenceId());
            }
            

            // Columns
            Map<String,String> jsFunctions = new LinkedHashMap<>();

            // TRATAR JsFunction antes de valueToTree
            Object cleanedColumns = replaceFunctions(
                    gridConfig.getColumns(),
                    jsFunctions);

            configNode.set("columns",
                    mapper.valueToTree(cleanedColumns));
            

            // Options Java (defaultOptions + locale + etc)
            ObjectNode javaOptions = getFinalOptions();

            Object cleanedOptions = replaceFunctions(
                    mapper.convertValue(javaOptions, Map.class),
                    jsFunctions);

            Object cleanedDeclarative = replaceFunctions(
                    gridConfig.getOptions(),
                    jsFunctions);

            ObjectNode declarativeOptions =
                    mapper.valueToTree(cleanedDeclarative);

            ObjectNode mergedJavaOptions =
                    mergeOptions(
                            mapper.valueToTree(cleanedOptions),
                            declarativeOptions
                    );
          
            configNode.set("options", mergedJavaOptions);

            String json = mapper.writeValueAsString(configNode);

            // Substituir placeholders
            for (Map.Entry<String,String> entry : jsFunctions.entrySet()) {
                String quotedKey = "\"" + entry.getKey() + "\"";
                json = json.replace(quotedKey, entry.getValue());
            }


			return "var " + tableVar
			        + " = window.tabulator.wicket.grid.create("
			        + json
			        + ");";
            
			
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public TabulatorJsonBehavior addAfterCreate(TableFunction fn) {
        if (fn != null) {
            addRunOnTableInit(fn);
        }
        return this;
    }
    
    @Override
    protected final void renderHeadResources(Component c, IHeaderResponse r) {
    	r.render(JavaScriptHeaderItem.forReference(TabulatorJsonReference.get()));
    	super.renderHeadResources(c, r);
    }
    
    private Object replaceFunctions(Object obj, Map<String,String> jsFunctions) {

        if (obj instanceof JsFunction fn) {
            jsFunctions.put(fn.getId(), fn.getSource());
            return fn.getId();
        }

        if (obj instanceof Map<?,?> map) {
            Map<Object,Object> newMap = new LinkedHashMap<>();
            for (var e : map.entrySet()) {
                newMap.put(
                    e.getKey(),
                    replaceFunctions(e.getValue(), jsFunctions)
                );
            }
            return newMap;
        }

        if (obj instanceof Collection<?> col) {
            List<Object> newList = new ArrayList<>();
            for (Object item : col) {
                newList.add(replaceFunctions(item, jsFunctions));
            }
            return newList;
        }

        return obj;
    }
}
