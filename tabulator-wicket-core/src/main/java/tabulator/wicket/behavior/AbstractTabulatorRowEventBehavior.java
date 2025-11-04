package tabulator.wicket.behavior;

import java.util.Map;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.util.string.StringValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import tabulator.wicket.TableRowData;
import tabulator.wicket.TabulatorWicketPlugin;

public abstract class AbstractTabulatorRowEventBehavior extends AbstractTabulatorAjaxBehavior {

	private static final Logger log = LoggerFactory.getLogger(AbstractTabulatorRowEventBehavior.class);
	
    private final String eventName;

    protected AbstractTabulatorRowEventBehavior(String eventName) {
        this.eventName = eventName;
    }

    @Override
    public String getEventName() { return eventName; }

    @Override
    protected String getJsCallbackArgs() { return "(e,row)"; }

    @Override
    protected String getJsPayloadExpression() {
        // gera dados da linha de forma estruturada
        //return "{rowIndex: row.getPosition(), rowId: row.getIndex(), rowData: row.getData()}";
        return "JSON.stringify({rowIndex: row.getPosition(), rowId: row.getIndex(), rowData: row.getData()})";

    }
	/*
	@Override
	protected void contributeExtraParameters(AjaxRequestAttributes attributes) {
	    attributes.getDynamicExtraParameters().add("return ["
	    		+ "{name: 'rowIndex', value: rowIndex}"
	    		+ ",{name: 'rowId', value: rowId}"
	    		+ ",{name:'rowDataJson',value:rowDataJson}"
	    		+ "];");
	}*/
    
    @Override
    protected Object parseEventPayload(IRequestParameters params, ObjectMapper mapper) {
        StringValue json = params.getParameterValue("eventPayload");
        log.debug("JSON: {}", json);
        try {
            Map<String, Object> raw = mapper.readValue(json.toString(), new TypeReference<>() {});
            Map<String, Object> rowData = (Map<String, Object>) raw.get("rowData");
            Integer rowIndex = (Integer) raw.get("rowIndex");
            String rowId = raw.get("rowId") != null ? raw.get("rowId").toString() : null;
            return new TableRowData(rowId, rowIndex, rowData);
        } catch (Exception e) {
        	log.error("Error parsing JSON", e);
            return new TableRowData(null, null, Map.of());
        }
    }

    @Override
    protected void onEvent(AjaxRequestTarget target, Object data) {
        onEvent(target, (TableRowData) data);
    }

    protected abstract void onEvent(AjaxRequestTarget target, TableRowData row);
}
