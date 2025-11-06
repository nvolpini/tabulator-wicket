package tabulator.wicket;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.util.MapModel;
import org.junit.jupiter.api.Test;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TabulatorTemplateInitializerTest extends AbstractWicketTest {

    @Test
    void testTemplateSubstitutionWorks() {
        ITabulatorInitializer initializer = new TabulatorTemplateInitializerModel(
                TabulatorTemplateInitializerTest.class,
                "dummy-template.js.tmpl",
                new MapModel<String, Object>(
                Map.of("extra", "ok"))
        );

        WebMarkupContainer c = new WebMarkupContainer("table");
        c.setMarkupId("myTable");

        String js = initializer.generateScript(c, "tab_myTable");
        assertTrue(js.contains("tab_myTable"));
        assertTrue(js.contains("ok"));
    }
}
