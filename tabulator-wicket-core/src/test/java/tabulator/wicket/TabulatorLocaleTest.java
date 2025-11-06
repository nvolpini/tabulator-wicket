package tabulator.wicket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;


public class TabulatorLocaleTest extends AbstractWicketTest {

    private ITabulatorSettings settings;
    private ObjectMapper mapper;

    @BeforeEach
    void setup2() {
        mapper = new ObjectMapper();
        settings = TabulatorWicketPlugin.settings();
    }

    @Test
    void testLazyTranslationNotLoadedInitially() {
        // Nenhuma tradução deve ser carregada antes do uso
        assertTrue(settings.getRegisteredTranslations().isEmpty(),
            "Traduções devem ser carregadas apenas no onBind()");
    }

    @Test
    void testLocaleAndLangsAppliedOnBind() {
        // Mock simples de initializer (não precisa gerar JS real)
        ITabulatorInitializer init = (c, var) -> "console.log('init ' + var);";

        // Cria o behavior
        TabulatorBehavior behavior = new TabulatorBehavior(init)
            .setLocale("pt-BR");

        DummyComponent component = new DummyComponent("tbl");

        // Mocka o header response apenas para garantir que onBind() executa
        AtomicBoolean called = new AtomicBoolean(false);
        IHeaderResponse mockResponse = new IHeaderResponse() {
            @Override
            public void render(HeaderItem item) {
                called.set(true);
            }
            @Override public void close() {}
            @Override public void markRendered(Object object) {}
            @Override public boolean wasRendered(Object object) { return false; }
			
			@Override
			public Response getResponse() {
				// TODO Auto-generated method stub
				return null;
			}
			@Override
			public boolean isClosed() {
				// TODO Auto-generated method stub
				return false;
			}
        };

        // Dispara o ciclo normal de bind/renderHead
        behavior.bind(component);
        behavior.renderHead(component, mockResponse);

        // Agora a tradução deve ter sido carregada no settings
        assertFalse(settings.getRegisteredTranslations().isEmpty(),
            "Tradução deve ser carregada após onBind() do behavior");

        // E o getFinalOptions() deve conter lang e langs
        ObjectNode opts = behavior.getFinalOptions();
        assertEquals("pt-BR", opts.get("lang").asText());
        assertTrue(opts.has("langs"), "As traduções devem ser aplicadas no merge final");
    }

    @Test
    void testGetTranslationLoadsResource() {
        var node = settings.getTranslation("pt-BR").orElseThrow();
        assertTrue(node.has("pt-BR"));
        assertTrue(node.get("pt-BR").has("pagination"));
    }
}
