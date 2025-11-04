package tabulator.wicket.behavior;

import java.util.List;
import java.util.Map;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.util.string.StringValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import tabulator.wicket.TabulatorWicketPlugin;

public abstract class AbstractTabulatorAjaxBehavior extends AbstractDefaultAjaxBehavior {

	private static final Logger log = LoggerFactory.getLogger(AbstractTabulatorAjaxBehavior.class);

	private boolean bound;
    
	public abstract String getEventName();

	/** JS handler args (ex: "(e,row)", "(data)", "(e,cell)") */
	protected abstract String getJsCallbackArgs();

	/** Callback de evento específico (a ser implementado nas subclasses) */
	protected abstract void onEvent(AjaxRequestTarget target, Object data);

	/**
	 * Permite personalizar o payload enviado pelo JS. Padrão: envia argumentos como
	 * JSON string.
	 */
	protected String getJsPayloadExpression() {
		return "JSON.stringify(Array.from(arguments))";
	}

	/**
	 * Permite ajustar a leitura do payload. Subclasses podem sobrescrever se o
	 * evento retornar array, objeto, etc.
	 */
	/*protected Object parseEventPayload(IRequestParameters params, ObjectMapper mapper) {
		StringValue payload = params.getParameterValue("eventPayload");
		if (payload.isEmpty()) {
			return Map.of();
		}
		String json = payload.toString();
		log.debug("JSON: {}", json);
		try {
			// Padrão: Map<String,Object>
			return mapper.readValue(json, new TypeReference<Map<String, Object>>() {
			});
		} catch (Exception e) {
			log.warn("Erro parsing JSON, event: '{}': {}", getEventName(), e.getMessage(), e);
			return Map.of("raw", json);
		}
	}*/
	
	protected Object parseEventPayload(IRequestParameters params, ObjectMapper mapper) {
	    StringValue payload = params.getParameterValue("eventPayload");
	    if (payload.isEmpty()) {
	       
	        return Map.of();
	    }
	    String json = payload.toString();
	    log.debug("JSON: {}", json);
		try {
		    JsonNode node = mapper.readTree(json);
		    if (node.isArray()) {
		        return mapper.convertValue(node, new TypeReference<List<Map<String,Object>>>(){});
		    } else if (node.isObject()) {
		        return mapper.convertValue(node, new TypeReference<Map<String,Object>>(){});
		    } else {
		        return mapper.convertValue(node, Object.class);
		    }
		} catch (Exception e) {
			log.error("Erro parsing JSON, event: '{}': {}", getEventName(), e.getMessage(), e);
			return Map.of("raw", json);
		}
	}

	/** Permite incluir parâmetros extras (além do payload principal). */
	protected void contributeExtraParameters(AjaxRequestAttributes attributes) {
		// subclasses podem adicionar mais atributos, se necessário
	}

	@Override
	protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
		super.updateAjaxAttributes(attributes);
		// parâmetro padrão: eventPayload
		attributes.getDynamicExtraParameters()
				.add("return [{name:'eventPayload', value:" + getJsPayloadExpression() + "}];");
		contributeExtraParameters(attributes);
	}

	public String createEventScript(String tableVar) {
		return String.format(
				"%s.on('%s', function%s {" + "try { new Wicket.Ajax.Call().ajax(%s); }"
						+ "catch(e){ console.error('Erro evento %s', e); }" + "});",
				tableVar, getEventName(), getJsCallbackArgs(), renderAjaxAttributes(getComponent()), getEventName());
	}

	@Override
	protected void respond(AjaxRequestTarget target) {
		ObjectMapper mapper = TabulatorWicketPlugin.settings().getObjectMapperFactory().newObjectMapper();
		Object data = parseEventPayload(getComponent().getRequest().getRequestParameters(), mapper);
		onEvent(target, data);
	}


    @Override
    protected void onBind() {
    	super.onBind();
    	log.debug("bind for component: {}"
        		, getComponent().getId());
        this.bound = true;
    }
    
    public boolean isBound() {
    	return bound;
    }
}
