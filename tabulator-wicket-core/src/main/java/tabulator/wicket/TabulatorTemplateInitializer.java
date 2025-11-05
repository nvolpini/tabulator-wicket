package tabulator.wicket;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.wicket.Component;

public class TabulatorTemplateInitializer implements ITabulatorInitializer {

    private final Class<?> contextClass;
    private final String templatePath;
    private final Map<String, Object> variables;
    private final String startToken;
    private final String endToken;

    public TabulatorTemplateInitializer(Class<?> contextClass, String templatePath
    		, Map<String, Object> variables) {
        this(contextClass, templatePath, variables, "__", "__"); // default
    }

    public TabulatorTemplateInitializer(Class<?> contextClass, String templatePath
    		, Map<String, Object> variables,
    			String startToken, String endToken) {
        this.contextClass = contextClass;
        this.templatePath = templatePath;
        this.variables = variables;
        this.startToken = startToken;
        this.endToken = endToken;
    }

    @Override
    public String generateScript(Component component, String tableVarName) {
        String content = loadTemplate();
        Map<String, Object> vars = new HashMap<>(variables);
        vars.put("markupId", component.getMarkupId());
        vars.put("tableVarName", tableVarName);

        for (var e : vars.entrySet()) {
        	
        	String value = Objects.toString(e.getValue(), "");
        	
            String placeholder = startToken + e.getKey() + endToken;
            content = content.replace(placeholder, value);
        }

        return content;
    }

    private String loadTemplate() throws TabulatorWicketException {
        try (InputStream in = contextClass.getResourceAsStream(templatePath)) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new TabulatorWicketException("Template não encontrado: " + templatePath, e);
		}
	}

}
