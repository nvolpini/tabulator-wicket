package tabulator.wicket;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class TabulatorUtilsTest extends AbstractWicketTest {

    @Test
    void testRunOnTableAppendsScript() {
        AjaxRequestTarget target = mock(AjaxRequestTarget.class);
        WebMarkupContainer comp = new WebMarkupContainer("tbl");
        comp.setMarkupId("tbl1");

        TabulatorUtils.runOnTable(target, comp, t -> t + ".reload();");

        verify(target).appendJavaScript("Tabulator.findTable('#tbl1')[0].reload();");
    }
}
