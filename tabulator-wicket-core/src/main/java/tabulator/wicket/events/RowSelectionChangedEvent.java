package tabulator.wicket.events;

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
import tabulator.wicket.behavior.AbstractTabulatorAjaxBehavior;

public abstract class RowSelectionChangedEvent extends AbstractTabulatorAjaxBehavior {

	private static final Logger log = LoggerFactory.getLogger(RowSelectionChangedEvent.class);

    @Override
    public String getEventName() {
        return "rowSelectionChanged";
    }

    /**
     *<pre> 
     *  //rows - array of row components for the currently selected rows in order of selection
     *  //data - array of data objects for the currently selected rows in order of selection
     *  //selected - array of row components that were selected in the last action
     *  //deselected - array of row components that were deselected in the last action
     * </pre>
     * 
     */
    @Override
    protected String getJsCallbackArgs() {
        return "(data, rows, selected, deselected)";
    }

    @Override
    protected String getJsPayloadExpression() {
        return "JSON.stringify(data)";
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
        onSelectionChanged(target, (List<TableRowData>) data);
    }

    protected abstract void onSelectionChanged(AjaxRequestTarget target, List<TableRowData> selectedRows);
}
