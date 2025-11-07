package tabulator.wicket;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
        var validator = new TabulatorInitializerValidator(ValidationMode.STRICT);
        ObjectNode node = validator.extractAndParseFromRenderedString(rawTemplate);

        assertTrue(node.has("columns"));
        assertTrue(node.get("pagination").asBoolean());
        assertTrue(node.get("columnDefaults").has("headerSort"));
    }

    @Test
    void testInvalidTemplateLenientDoesNotThrow() {
        var validator = new TabulatorInitializerValidator(ValidationMode.LENIENT);

        assertDoesNotThrow(() -> validator.extractAndParseFromRenderedString("const t = new Tabulator('#id',{ invalid, });"));
    }

    @Test
    void testInvalidTemplateStrictThrows() {
        //ITabulatorInitializer init = new TabulatorStringInitializer("const t = new Tabulator('#id',{ invalid, });");
        var validator = new TabulatorInitializerValidator(ValidationMode.STRICT);

        assertThrows(TabulatorWicketException.class,
            () -> validator.extractAndParseFromRenderedString("const t = new Tabulator('#id',{ invalid, });"));
    }
    

    @Test
    void testTemplateVariableIsInterpolatedAndMerged() {
        ITabulatorInitializer init = new TabulatorTemplateInitializerModel(
            TabulatorInitializerValidatorTest.class,
            "table-template.js",
            new MapModel<>(Map.of("url", "/api/test"))
        );

        TabulatorBehavior behavior = new TabulatorBehavior(init);
        behavior.options().addColumnDefault("headerSort", true);

        var validator = new TabulatorInitializerValidator(ValidationMode.STRICT);

        // Simula bind+renderHead: obtém renderedTemplate, extract, merge, finalScript
        String rendered = init.generateScript(dummy, "table1");
        ObjectNode templateOpts = validator.extractAndParseFromRenderedString(rendered);
        ObjectNode merged = behavior.mergeOptions(templateOpts, behavior.getFinalOptions());
        String mergedJson = merged.toPrettyString();
        String finalScript = behavior.replaceTabulatorOptions(rendered, mergedJson);

        assertFalse(finalScript.contains("__url__"));
        assertTrue(finalScript.contains("/api/test")); // variável resolvida
        assertTrue(finalScript.contains("\"headerSort\"")); // merge aplicado
    }
    @Test
    void testFunctionIsHandledAndRestored() {
        var validator = new TabulatorInitializerValidator(ValidationMode.STRICT);

        // Faz o parse (as funções serão substituídas por placeholders internamente)
        ObjectNode node = validator.extractAndParseFromRenderedString(rawTemplate);

        // o parse deve funcionar mesmo com funções JS
        assertNotNull(node);
        assertTrue(node.has("columns"), "O objeto parseado deve conter 'columns'");

        // Gera JSON de volta e restaura as funções
        String mergedJson = node.toPrettyString();
        String restored = validator.restoreFunctions(mergedJson);

        // --- Asserts principais ---
        assertTrue(restored.contains("function(cell, formatterParams, onRender)"),
                "A função original deve ter sido restaurada no script final");

        assertTrue(restored.contains("(cell) =>"),
                "A função arrow deve ter sido restaurada no script final");
        
        assertTrue(restored.contains("return value.substring"),
                "O corpo da função deve ter sido preservado");
        assertFalse(restored.contains("__FUNC_"),
                "Nenhum placeholder de função deve restar no resultado final");
    }

}
