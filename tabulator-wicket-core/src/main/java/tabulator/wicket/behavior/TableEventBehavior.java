package tabulator.wicket.behavior;

import java.util.List;
import java.util.Map;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.request.IRequestParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class TableEventBehavior extends AbstractTabulatorAjaxBehavior {

	private static final Logger log = LoggerFactory.getLogger(TableEventBehavior.class);
	
    @Override
    protected String getJsCallbackArgs() { return "(data)"; }

    @Override
    protected String getJsPayloadExpression() {
        // envia o array de dados diretamente
        return "JSON.stringify(data)";
    }

    @Override
    protected Object parseEventPayload(IRequestParameters params, ObjectMapper mapper) {
        String json = params.getParameterValue("eventPayload").toString();
        try {
            return mapper.readValue(json, new TypeReference<List<Map<String,Object>>>() {});
        } catch (Exception e) {
            log.warn("Erro parseando payload para TableEventBehavior: {}", e.getMessage());
            return List.of();
        }
    }

    @Override
    protected void onEvent(AjaxRequestTarget target, Object data) {
        onTableEvent(target, (List<Map<String,Object>>) data);
    }

    protected abstract void onTableEvent(AjaxRequestTarget target, List<Map<String,Object>> data);
}
