package tabulator.wicket;

import org.apache.wicket.Component;
import org.apache.wicket.util.template.PackageTextTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TabulatorTemplateInitializer2 implements ITabulatorInitializer {
	private final Class<?> scope;
	private final String templateName;
	private final Map<String, Object> variables;

	public TabulatorTemplateInitializer2(Class<?> scope, String templateName, Map<String, Object> vars) {
		this.scope = scope;
		this.templateName = templateName;
		this.variables = vars != null ? vars : Map.of();
	}

	@Override
	public String generateScript(Component component, String tableVarName) {
		try (PackageTextTemplate tmpl = new PackageTextTemplate(scope, templateName)) {
			Map<String, Object> vars = new HashMap<>(variables);
			vars.put("markupId", component.getMarkupId());
			vars.put("tableVarName", tableVarName);
			return tmpl.asString(vars);
		} catch (IOException e) {
			throw new TabulatorWicketException("Error parsing template",e);
		}
	}
}