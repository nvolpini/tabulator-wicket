package tabulator.wicket.events;

import tabulator.wicket.behavior.AbstractTabulatorRowEventBehavior;

public abstract class RowClickEvent extends AbstractTabulatorRowEventBehavior {
    public RowClickEvent() {
        super("rowClick");
    }
}
