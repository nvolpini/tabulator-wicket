package tabulator.wicket.events;

import tabulator.wicket.behavior.TableEventBehavior;

public abstract class DataLoadedEvent extends TableEventBehavior {

	@Override
	public String getEventName() {
		return "dataLoaded";
	}

}
