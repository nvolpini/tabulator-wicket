package tabulator.wicket.events;

import tabulator.wicket.behavior.AbstractTabulatorRowEventBehavior;

public abstract class RowContextEvent extends AbstractTabulatorRowEventBehavior {
    public RowContextEvent() {
        super("rowContext");
    }
}
