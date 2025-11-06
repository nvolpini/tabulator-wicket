package tabulator.wicket;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.util.MapModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

import tabulator.wicket.TabulatorInitializerValidator.ValidationMode;

class DummyComponent extends Label {
    protected DummyComponent(String id) { super(id); }

}

public class TabulatorInitializerValidatorTest extends AbstractWicketTest {

    private String rawTemplate;
    private DummyComponent dummy;

    @BeforeEach
    void setup2() throws Exception {
        rawTemplate = Files.readString(Path.of("src/test/resources/tabulator/wicket/table-template.js"));
        dummy = new DummyComponent("tbl");
    }

    @Test
    void testValidTemplateIsParsed() {
        //ITabulatorInitializer init = new TabulatorStringInitializer(rawTemplate);
        var validator = new TabulatorInitializerValidator(ValidationMode.STRICT);

        ObjectNode node = validator.extractAndParseFromRenderedString(rawTemplate);
        assertTrue(node.has("columns"));
        assertTrue(node.get("pagination").asBoolean());
    }

    @Test
    void testInvalidTemplateLenientDoesNotThrow() {
        //ITabulatorInitializer init = new TabulatorStringInitializer("const t = new Tabulator('#id',{ invalid, });");
        var validator = new TabulatorInitializerValidator(ValidationMode.LENIENT);

        assertDoesNotThrow(() -> validator.extractAndParseFromRenderedString(rawTemplate));
    }

    @Test
    void testInvalidTemplateStrictThrows() {
        //ITabulatorInitializer init = new TabulatorStringInitializer("const t = new Tabulator('#id',{ invalid, });");
        var validator = new TabulatorInitializerValidator(ValidationMode.STRICT);

        assertThrows(TabulatorWicketException.class,
            () -> validator.extractAndParseFromRenderedString("const t = new Tabulator('#id',{ invalid, });"));
    }
    
    @Test
    void testBehaviorGeneratesMergedScript() {
        ITabulatorInitializer init = new TabulatorStringInitializer(rawTemplate);

        TabulatorBehavior behavior = new TabulatorBehavior(init);
        behavior
            .options()
                .set("paginationSize", 50)
                .set("locale", true)
                .addColumnDefault("hozAlign", "center");

        ObjectNode finalOpts = behavior.getFinalOptions();

        assertEquals(true, finalOpts.get("locale").asBoolean());
        assertEquals(50, finalOpts.get("paginationSize").asInt());
        assertEquals("\"center\"", finalOpts.get("columnDefaults").get("hozAlign").toString());
    }
    
    @Test
    void testTemplateVariableIsInterpolatedAndMerged() {
        
        ITabulatorInitializer init = new TabulatorTemplateInitializerModel(TabulatorInitializerValidatorTest.class
        		, "table-template.js", new MapModel<>(Map.of("url", "/api/test")));

        TabulatorBehavior behavior = new TabulatorBehavior(init);
        behavior.options().addColumnDefault("headerSort", true);
        
        var validator = new TabulatorInitializerValidator(ValidationMode.STRICT);

        // Simula bind+renderHead: obtém renderedTemplate, extract, merge, finalScript
        String rendered = init.generateScript(dummy, "table1");
        ObjectNode templateOpts = validator.extractAndParseFromRenderedString(rendered);
        ObjectNode merged = behavior.mergeOptions(templateOpts, behavior.getFinalOptions());
        String finalScript = behavior.replaceTabulatorOptions(rendered, merged.toPrettyString());

        assertFalse(finalScript.contains("__url__"));
        assertTrue(finalScript.contains("/api/test")); // valor interpolado
        assertTrue(finalScript.contains("\"headerSort\"")); // vindo do merged columnDefaults
    }
}
