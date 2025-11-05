package tabulator.wicket;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.wicket.Component;
import org.apache.wicket.model.IDetachable;
import org.apache.wicket.model.IModel;

public abstract class AbstractTabulatorTemplateInitializer implements ITabulatorInitializer, IDetachable {

	protected final IModel<Map<String, Object>> variablesModel;
	protected final String startToken;
	protected final String endToken;

	protected AbstractTabulatorTemplateInitializer(IModel<Map<String, Object>> variablesModel, String startToken,
			String endToken) {
		this.variablesModel = variablesModel;
		this.startToken = startToken;
		this.endToken = endToken;
	}

	protected abstract String loadTemplate();

	@Override
	public String generateScript(Component component, String tableVarName) {
		String content = loadTemplate();

		Map<String, Object> vars = Optional.ofNullable(variablesModel.getObject()).map(HashMap::new)
				.orElseGet(HashMap::new);

		vars.put("markupId", component.getMarkupId());
		vars.put("tableVarName", tableVarName);

		for (var e : vars.entrySet()) {
			String placeholder = startToken + e.getKey() + endToken;
			String value = Objects.toString(e.getValue(), "");
			content = content.replace(placeholder, value);
		}

		return content;
	}

	@Override
	public void detach() {
		if (variablesModel != null) {
			variablesModel.detach();
		}
	}
}
