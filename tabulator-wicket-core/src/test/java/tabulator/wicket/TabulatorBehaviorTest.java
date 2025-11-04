package tabulator.wicket;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TabulatorBehaviorTest extends AbstractWicketTest {

    @Test
    void testRenderHeadAddsResources() {
        WebMarkupContainer table = new WebMarkupContainer("table");
        table.setMarkupId("t1");

        
        ITabulatorInitializer initializer = (component, tableVar) -> "console.log('" + tableVar + "');";

        TabulatorBehavior behavior = new TabulatorBehavior(initializer);
        table.add(behavior);

        tester.startComponentInPage(table);

        String html = tester.getLastResponseAsString();
        assertTrue(html.contains("id=\"t1\""));
    }
}
