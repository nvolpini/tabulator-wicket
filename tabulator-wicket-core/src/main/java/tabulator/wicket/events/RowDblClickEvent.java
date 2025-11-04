package tabulator.wicket.events;

import tabulator.wicket.behavior.AbstractTabulatorRowEventBehavior;

public abstract class RowDblClickEvent extends AbstractTabulatorRowEventBehavior {
    public RowDblClickEvent() {
        super("rowDblClick");
    }
}
