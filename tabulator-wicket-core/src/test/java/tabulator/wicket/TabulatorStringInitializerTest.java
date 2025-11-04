package tabulator.wicket;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.wicket.util.tester.WicketTester;
import org.junit.jupiter.api.Test;

import tabulator.wicket.TabulatorStringInitializer;

public class TabulatorStringInitializerTest extends AbstractWicketTest {
    @Test
    public void testStringReplacement() {
    	
        String jsBase = "table('${markupId}','${tableVarName}');";
        TabulatorStringInitializer initializer = new TabulatorStringInitializer(jsBase);
        org.apache.wicket.markup.html.WebMarkupContainer comp = new org.apache.wicket.markup.html.WebMarkupContainer("t");
        comp.setMarkupId("tab1");

        String script = initializer.generateScript(comp, "cb");
        assertEquals("table('tab1','cb');", script);
    }
}