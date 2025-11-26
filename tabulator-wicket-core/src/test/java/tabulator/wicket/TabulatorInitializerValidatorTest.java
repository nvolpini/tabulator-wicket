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

import com.fasterxml.jackson.databind.ObjectMapper;
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
    
    @Test
    void testPreambleAndPostambleArePreserved() {
        var validator = new TabulatorInitializerValidator(ValidationMode.STRICT);

        TabulatorTemplateParts parts = validator.extractTemplateParts(rawTemplate);

        assertTrue(parts.preamble().contains("function testFunctionPre()"),
                "Função antes do Tabulator deve permanecer no preamble");

        assertTrue(parts.preamble().contains("var statusContextMenu"),
                "Variáveis globais antes do Tabulator devem permanecer");

        assertTrue(parts.postamble().contains("function testFunctionPos()"),
                "Função após o Tabulator deve permanecer no postamble");
    }
    @Test
    void testFullTemplateReconstruction() {
        var validator = new TabulatorInitializerValidator(ValidationMode.STRICT);

        TabulatorTemplateParts parts = validator.extractTemplateParts(rawTemplate);

        ObjectNode templateJson = validator.parseJsonObject(parts.jsonObject());
        String restoredJson = validator.restoreFunctions(templateJson.toPrettyString());

        String finalScript = parts.preamble() + restoredJson + parts.postamble();

        // ✔️ preamble intacto
        assertTrue(finalScript.contains("function testFunctionPre()"));
        assertTrue(finalScript.contains("var statusContextMenu"));

        // ✔️ postamble intacto
        assertTrue(finalScript.contains("function testFunctionPos()"));

        // ✔️ funções internas restauradas
        assertTrue(finalScript.contains("function(cell, formatterParams, onRender)"));
        assertTrue(finalScript.contains("(cell) => {"));

        // ✔️ nada de placeholders restantes
        assertFalse(finalScript.contains("__FUNC_"));
    }

    @Test
    void testArraysAndGlobalFunctionsUnaffected() {
        var validator = new TabulatorInitializerValidator(ValidationMode.STRICT);

        TabulatorTemplateParts parts = validator.extractTemplateParts(rawTemplate);

        System.out.println("PRE");
        System.out.println(parts.preamble());

        System.out.println("JSON");
        System.out.println(parts.jsonObject());

        System.out.println("POST");
        System.out.println(parts.postamble());

        // 1) A definição do array deve estar no preamble
        assertTrue(parts.preamble().contains("var statusContextMenu"),
                "A definição do array statusContextMenu deve permanecer no preamble");

        // 2) O jsonObject NÃO deve conter a definição (ex.: 'var statusContextMenu = [')
        assertFalse(parts.jsonObject().contains("var statusContextMenu"),
                "A definição do array não deve ser movida para o JSON do Tabulator");

        // 3) O jsonObject pode (e deve) conter referência ao símbolo (contextMenu: statusContextMenu)
        assertTrue(parts.jsonObject().contains("statusContextMenu"),
                "O jsonObject deve referenciar statusContextMenu (como referência), não redefini-lo");

        // parse + restore
        ObjectNode json = validator.parseJsonObject(parts.jsonObject());
        String restored = validator.restoreFunctions(json.toPrettyString());

        String finalScript = parts.preamble() + restored + parts.postamble();

        // 4) O script final deve conter o array/global intacto (definição no preamble)
        assertTrue(finalScript.contains("statusContextMenu"),
                "O array global deve continuar existindo no template final");

        // E garantir que não duplicamos a definição dentro do JSON ao reconstruir:
        assertTrue(finalScript.contains("var statusContextMenu"), "Definição não deve desaparecer");
        assertFalse(finalScript.contains("var statusContextMenu") && parts.jsonObject().contains("var statusContextMenu"),
                "A definição não deve ser movida para dentro do JSON");
    }

    @Test
    void testOnlyTabulatorObjectIsParsed() {
        var validator = new TabulatorInitializerValidator(ValidationMode.STRICT);

        TabulatorTemplateParts parts = validator.extractTemplateParts(rawTemplate);

        ObjectNode obj = validator.parseJsonObject(parts.jsonObject());

        // chaves esperadas no objeto JSON
        assertTrue(obj.has("layout"));
        assertTrue(obj.has("columns"));
        assertTrue(obj.has("columnDefaults"));
        assertTrue(obj.has("pagination"));

        // chaves externas NÃO podem existir
        assertFalse(obj.has("testFunctionPre"));
        assertFalse(obj.has("statusContextMenu"));
        assertFalse(obj.has("__tableVarName__"));
    }
    
    @Test
    void testMergeDoesNotAffectPreambleOrPostamble() {
        var validator = new TabulatorInitializerValidator(ValidationMode.STRICT);
        TabulatorTemplateParts parts = validator.extractTemplateParts(rawTemplate);

        ObjectNode templateOpts = validator.parseJsonObject(parts.jsonObject());

        // merge simula opções do Behavior
        ObjectNode override = new ObjectMapper().createObjectNode();
        override.put("paginationSize", 999);

        TabulatorBehavior behavior = new TabulatorBehavior(
                new TabulatorStringInitializer(rawTemplate)
        );
        ObjectNode merged = behavior.mergeOptions(templateOpts, override);

        String mergedJson = validator.restoreFunctions(merged.toPrettyString());
        String finalScript = parts.preamble() + mergedJson + parts.postamble();

        // ❗ merge não pode jamais alterar preamble/postamble
        assertTrue(finalScript.contains("function testFunctionPre()"));
        assertTrue(finalScript.contains("function testFunctionPos()"));
        assertTrue(finalScript.contains("statusContextMenu"));
    }

}
