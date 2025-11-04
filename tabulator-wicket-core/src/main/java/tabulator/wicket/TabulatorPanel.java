package tabulator.wicket;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;

import tabulator.wicket.events.RowClickEvent;

public class TabulatorPanel extends Panel {
	private final TabulatorBehavior behavior;

	public TabulatorPanel(String id, ITabulatorInitializer initializer) {
		super(id);
		setOutputMarkupId(true);
		behavior = new TabulatorBehavior(initializer);
		add(behavior);

		behavior.add(new RowClickEvent() {
		    @Override
		    protected void onEvent(AjaxRequestTarget target, TableRowData row) {
		    	TabulatorPanel.this.onRowClick(target, row);
		    	
		    }
		});
	}

	protected void onRowClick(AjaxRequestTarget target, TableRowData rowData) {
	}


	public TabulatorBehavior getBehavior() {
		return behavior;
	}
}