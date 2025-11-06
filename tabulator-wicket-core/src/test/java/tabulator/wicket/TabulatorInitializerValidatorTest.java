package tabulator.wicket;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.wicket.markup.html.basic.Label;
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
        ITabulatorInitializer init = new TabulatorStringInitializer(rawTemplate);
        var validator = new TabulatorInitializerValidator(ValidationMode.STRICT);

        ObjectNode node = validator.validateAndExtract(dummy, init);
        assertTrue(node.has("columns"));
        assertTrue(node.get("pagination").asBoolean());
    }

    @Test
    void testInvalidTemplateLenientDoesNotThrow() {
        ITabulatorInitializer init = new TabulatorStringInitializer("const t = new Tabulator('#id',{ invalid, });");
        var validator = new TabulatorInitializerValidator(ValidationMode.LENIENT);

        assertDoesNotThrow(() -> validator.validateAndExtract(dummy, init));
    }

    @Test
    void testInvalidTemplateStrictThrows() {
        ITabulatorInitializer init = new TabulatorStringInitializer("const t = new Tabulator('#id',{ invalid, });");
        var validator = new TabulatorInitializerValidator(ValidationMode.STRICT);

        assertThrows(TabulatorWicketException.class,
            () -> validator.validateAndExtract(dummy, init));
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

}
