package tabulator.wicket.listeners;

import org.apache.wicket.ajax.AjaxRequestTarget;

public interface SelectionChangeListener {
    void onSelectionChanged(AjaxRequestTarget target, String idsJson);
}