package tabulator.wicket;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.wicket.Application;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.model.IModel;

public class TabulatorTemplateInitializerModel extends AbstractTabulatorTemplateInitializer {

    private final Class<?> contextClass;
    private final String templatePath;
    private static final Map<String, String> TEMPLATE_CACHE = new ConcurrentHashMap<>();

    public TabulatorTemplateInitializerModel(Class<?> contextClass, String templatePath,
                                             IModel<Map<String, Object>> variablesModel) {
        this(contextClass, templatePath, variablesModel, "__", "__");
    }

    public TabulatorTemplateInitializerModel(Class<?> contextClass, String templatePath,
                                             IModel<Map<String, Object>> variablesModel,
                                             String startToken, String endToken) {
        super(variablesModel, startToken, endToken);
        this.contextClass = contextClass;
        this.templatePath = templatePath;
    }

    @Override
    protected String loadTemplate() {
        Application app = Application.get();
        if (app.getConfigurationType() == RuntimeConfigurationType.DEVELOPMENT) {
            return loadTemplateInternal();
        }
        return TEMPLATE_CACHE.computeIfAbsent(contextClass.getName() + ":" + templatePath, key -> loadTemplateInternal());
    }

    private String loadTemplateInternal() {
        try (InputStream in = contextClass.getResourceAsStream(templatePath)) {
            if (in == null) {
                throw new TabulatorWicketException("Template não encontrado: " + templatePath);
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new TabulatorWicketException("Erro lendo template: " + templatePath, e);
        }
    }
}
