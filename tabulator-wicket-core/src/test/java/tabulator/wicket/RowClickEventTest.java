package tabulator.wicket;

import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.junit.jupiter.api.Test;
import tabulator.wicket.events.RowClickEvent;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

public class RowClickEventTest extends AbstractWicketTest {

    @Test
    void testRowClickEventTriggers() throws Exception {
        WebMarkupContainer table = new WebMarkupContainer("table");
        table.setMarkupId("tbl1");

        AtomicBoolean triggered = new AtomicBoolean(false);

        RowClickEvent event = new RowClickEvent() {
            @Override
            protected void onEvent(AjaxRequestTarget target, TableRowData rowData) {
                triggered.set(true);
                assertEquals("Widget", rowData.getString("name").orElse(null));
            }
        };

        table.add(event);
        tester.startComponentInPage(table);

        // Simula o request Ajax manualmente (injeção de parâmetro JSON)
        tester.getRequest().setParameter("rowIndex", "1");
        tester.getRequest().setParameter("rowDataJson", "{\"id\":1,\"name\":\"Widget\"}");

        // Executa o comportamento Ajax (como se o JS do Tabulator tivesse chamado)
        tester.executeBehavior((AbstractDefaultAjaxBehavior) event);

        assertTrue(triggered.get(), "Evento RowClickEvent deveria ter sido acionado");
    }
}
