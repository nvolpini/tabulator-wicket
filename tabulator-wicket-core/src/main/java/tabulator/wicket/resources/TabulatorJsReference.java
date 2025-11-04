package tabulator.wicket.resources;

import org.apache.wicket.request.resource.JavaScriptResourceReference;

import de.agilecoders.wicket.webjars.request.resource.WebjarsJavaScriptResourceReference;

public class TabulatorJsReference extends JavaScriptResourceReference {
    private static final long serialVersionUID = 1L;

    private static final TabulatorJsReference INSTANCE =
        new TabulatorJsReference();

    private static final WebjarsJavaScriptResourceReference WEBJARS_INSTANCE =
    		new WebjarsJavaScriptResourceReference("tabulator-tables/6.3.1/dist/js/tabulator.min.js");

    
    public static WebjarsJavaScriptResourceReference getWebjars() {
        return WEBJARS_INSTANCE;
    }

    public static TabulatorJsReference get() {
        return INSTANCE;
    }

    private TabulatorJsReference() {
        super(TabulatorJsReference.class,
              "/webjars/tabulator-tables/6.3.1/dist/js/tabulator.min.js");
    }
}
