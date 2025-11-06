package tabulator.wicket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class TabulatorBehaviorMergeTest {

    private ObjectMapper mapper;

    @BeforeEach
    void setup() {
        mapper = new ObjectMapper();
    }

    @Test
    void testRecursiveMergeOfOptions() {
        ObjectNode template = mapper.createObjectNode();
        ObjectNode columnDefaults = mapper.createObjectNode();
        columnDefaults.put("headerFilter", true);
        template.set("columnDefaults", columnDefaults);

        template.put("layout", "fitColumns");
        template.put("pagination", false);

        // Simula default options globais
        TabulatorDefaultOptions defaults = new TabulatorDefaultOptions()
            .set("pagination", true)
            .addColumnDefault("headerSort", true);

        // Merge
        ObjectNode merged = new TabulatorBehavior(
        		(c, tableVar) -> ""
        		)
            .mergeOptions(template, defaults.asJson());

        // Verifica merges recursivos
        assertTrue(merged.get("pagination").asBoolean(), "pagination deve ser sobrescrito pelo default");
        assertEquals("fitColumns", merged.get("layout").asText());

        ObjectNode cd = (ObjectNode) merged.get("columnDefaults");
        assertTrue(cd.get("headerFilter").asBoolean());
        assertTrue(cd.get("headerSort").asBoolean(), "merge recursivo deve incluir headerSort");
    }
}
