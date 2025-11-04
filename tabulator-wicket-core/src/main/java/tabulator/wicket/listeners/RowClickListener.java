package tabulator.wicket.listeners;

import org.apache.wicket.ajax.AjaxRequestTarget;

public interface RowClickListener {
    void onRowClick(AjaxRequestTarget target, String rowJson);
}