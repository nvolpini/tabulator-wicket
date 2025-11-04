package tabulator.wicket.listeners;

import org.apache.wicket.ajax.AjaxRequestTarget;

public interface RowContextListener {
    void onRowContext(AjaxRequestTarget target, String rowJson);
}