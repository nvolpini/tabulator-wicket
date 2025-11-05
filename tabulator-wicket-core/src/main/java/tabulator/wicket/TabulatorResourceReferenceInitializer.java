package tabulator.wicket;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.wicket.Application;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;

public class TabulatorResourceReferenceInitializer extends AbstractTabulatorTemplateInitializer {

    private final ResourceReference resourceReference;
    private static final Map<String, String> TEMPLATE_CACHE = new ConcurrentHashMap<>();

    public TabulatorResourceReferenceInitializer(ResourceReference resourceReference,
                                                 IModel<Map<String, Object>> variablesModel) {
        this(resourceReference, variablesModel, "__", "__");
    }

    public TabulatorResourceReferenceInitializer(ResourceReference resourceReference,
                                                 IModel<Map<String, Object>> variablesModel,
                                                 String startToken, String endToken) {
        super(variablesModel, startToken, endToken);
        this.resourceReference = resourceReference;
    }

    @Override
    protected String loadTemplate() {
        // Em DEV lemos sempre para facilitar o refresh imediato
        if (Application.get().getConfigurationType() == RuntimeConfigurationType.DEVELOPMENT) {
            return loadTemplateInternal();
        }

        final String key = resourceReference.getScope().getName() + ":" + resourceReference.getName();
        return TEMPLATE_CACHE.computeIfAbsent(key, k -> loadTemplateInternal());
    }
    
    private String loadTemplateInternal() {

    	
    	IResourceStream stream = null;
        try {
            // Localiza recurso via ResourceStreamLocator usando scope + name do ResourceReference
            Class<?> scope = resourceReference.getScope();
            String name = resourceReference.getName();
            stream = Application.get().getResourceSettings().getResourceStreamLocator().locate(scope, name);

            if (stream == null) {
                throw new TabulatorWicketException("Recurso não encontrado: " + resourceReference);
            }

            try (InputStream in = stream.getInputStream()) {
            	
            	//return IOUtils.toString(in);
            	
                return new String(in.readAllBytes(), StandardCharsets.UTF_8);
            }

        } catch (ResourceStreamNotFoundException | IOException e) {
            throw new TabulatorWicketException("Erro ao ler recurso: " + resourceReference, e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException ignored) { }
            }
        }
    }
}
