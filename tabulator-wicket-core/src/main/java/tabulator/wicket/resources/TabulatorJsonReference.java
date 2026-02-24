package tabulator.wicket.resources;

import org.apache.wicket.request.resource.JavaScriptResourceReference;

import de.agilecoders.wicket.webjars.request.resource.WebjarsJavaScriptResourceReference;
import tabulator.grid.wicket.TabulatorJsonBehavior;

public class TabulatorJsonReference extends JavaScriptResourceReference {
    private static final long serialVersionUID = 1L;

    private static final TabulatorJsonReference INSTANCE =
        new TabulatorJsonReference();

    public static TabulatorJsonReference get() {
        return INSTANCE;
    }

    private TabulatorJsonReference() {
        super(TabulatorJsonBehavior.class,
              "tabulator-wicket-grid.js");
    }
}
