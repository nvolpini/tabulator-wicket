package tabulator.wicket;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.IMarkupResourceStreamProvider;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringBufferResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.jupiter.api.Test;

import tabulator.wicket.TabulatorPanel;
import tabulator.wicket.TabulatorSettings;
import tabulator.wicket.TabulatorTemplateInitializer;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TabulatorPanelTest {

    public static class TestPage extends WebPage implements IMarkupResourceStreamProvider {
        public TestPage() {
            add(new TabulatorPanel("tabela",
                    
            		(c, tableVar) -> "console.log('init " + tableVar + "');"
            ));
        }

		@Override
		public IResourceStream getMarkupResourceStream(MarkupContainer container, Class<?> containerClass) {
			return new StringResourceStream("<div wicket:id=\"tabela\"></div>");
		}
    }

    @Test
    public void testPanelRendersWithTableDiv() {
        WicketTester tester = new WicketTester();
        
        TabulatorWicketPlugin.install(tester.getApplication());
        
        
        tester.startPage(TestPage.class);
        String html = tester.getLastResponseAsString();
        assertTrue(html.contains("tabela"));
    }
}