package tabulator.wicket.behavior;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.request.IRequestParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import tabulator.wicket.TableRowData;
import tabulator.wicket.TabulatorUtils;

/**
 * Sends the current selection (table.getSelectedData()) to the callback {@link #onSelectionSubmit(AjaxRequestTarget, List)}
 */
public abstract class SelectionSubmitBehavior extends AbstractTabulatorAjaxBehavior {
	
	private static final Logger log = LoggerFactory.getLogger(SelectionSubmitBehavior.class);

    @Override
    public String getEventName() {
        return "selectionSubmit";
    }

    @Override
    protected String getJsCallbackArgs() {
        return "()"; 
    }

    @Override
    protected String getJsPayloadExpression() {
        return "JSON.stringify("
        		+ TabulatorUtils.findTableJavascript(getComponent())
        				+ ".getSelectedData())";
    }

    @Override
    protected Object parseEventPayload(IRequestParameters params, ObjectMapper mapper) {
        String json = params.getParameterValue("eventPayload").toString("");
        log.debug("JSON: {}", json);
        
        try {
        	
            List<Map<String, Object>> res = mapper.readValue(json, new TypeReference<List<Map<String,Object>>>() {});
            
            //convert to List<TableRowData>
            return res.stream().map(l->new TableRowData(l)).collect(Collectors.toList());
            
            
        } catch (Exception e) {
        	log.error("Erro parsing JSON, event: '{}': {}", getEventName(), e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    protected void onEvent(AjaxRequestTarget target, Object data) {
        onSelectionSubmit(target, (List<TableRowData>) data);
    }

    protected abstract void onSelectionSubmit(AjaxRequestTarget target, List<TableRowData> selectedRows);

	public final void run(AjaxRequestTarget target) {
		
		target.appendJavaScript("new Wicket.Ajax.Call().ajax(" + this.renderAjaxAttributes(getComponent()) + ");");
		/*
		TabulatorUtils.runOnTable(target, getComponent(),
		        t -> "new Wicket.Ajax.Call().ajax(" + this.renderAjaxAttributes(getComponent()) + ");"
		    );*/
		
	}
}
