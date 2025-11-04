package tabulator.wicket.behavior;

import java.util.Map;
import java.util.TreeMap;

import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
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

/**
 * Base for Row events. Like:  'rowClick', 'rowContext'
 * 
 */
public abstract class AbstractTabulatorRowEventBehaviorx extends AbstractDefaultAjaxBehavior {

    private static final Logger log = LoggerFactory.getLogger(AbstractTabulatorRowEventBehaviorx.class);

    private final String eventName;

	private boolean bound;
    

    protected AbstractTabulatorRowEventBehaviorx(String eventName) {
        this.eventName = eventName;
    }

    public String getEventName() {
        return eventName;
    }

    @Override
    protected void onBind() {
    	super.onBind();
    	log.debug("bind for component: {}"
        		, getComponent().getId());
        this.bound = true;
    }
    
    @Override
    protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
        super.updateAjaxAttributes(attributes);
        // envia dados extras da linha
        attributes.getDynamicExtraParameters().add("return ["
        		+ "{name: 'rowIndex', value: rowIndex}"
        		+ ",{name: 'rowId', value: rowId}"
        		+ ",{name:'rowDataJson',value:rowDataJson}"
        		+ "];");
    }

    private CharSequence getAttrs() {
        return renderAjaxAttributes(getComponent());
    }

    /** Código JS que será anexado no init do Tabulator. */
    public String createEventScript(String tableVarName) {
        return String.format(
            "%1$s.on('%2$s', function(e,row) {" +
                "try{" +
        		 "var rowIndex=row.getPosition();" +
        		 "var rowId=row.getIndex();" +
                "var rowData=row.getData();" +
                "var rowDataJson=JSON.stringify(rowData);" +
                "new Wicket.Ajax.Call().ajax(%3$s);" +
                "}catch(error){console.error('Erro em %2$s',error);}" +
            "});",
            tableVarName, eventName, getAttrs());
    }

    @Override
    protected void respond(AjaxRequestTarget target) {
        IRequestParameters params = getComponent().getRequest().getRequestParameters();
        StringValue rowIndex = params.getParameterValue("rowIndex");
        StringValue rowId = params.getParameterValue("rowId");

        StringValue rowDataJson = params.getParameterValue("rowDataJson");

        Map<String, Object> rowData = new TreeMap<>();
        ObjectMapper mapper = TabulatorWicketPlugin.settings().getObjectMapperFactory().newObjectMapper();

        try {
            if (!rowDataJson.isEmpty()) {
                rowData = mapper.readValue(rowDataJson.toString(),
                        new TypeReference<Map<String, Object>>() {});
            }
        } catch (Exception e) {
            log.error("Erro ao converter JSON do Tabulator", e);
        }

        onEvent(target, new TableRowData(rowId.toOptionalString(), rowIndex.toOptionalInteger(), rowData));
    }

    /** Evento que o usuário deve implementar. */
    protected abstract void onEvent(AjaxRequestTarget target, TableRowData rowData);
    
    public boolean isBound() {
    	return bound;
    }
}
