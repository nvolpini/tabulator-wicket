package tabulator.wicket.resources;

import org.apache.wicket.request.resource.CssResourceReference;

import de.agilecoders.wicket.webjars.request.resource.WebjarsCssResourceReference;

public class TabulatorCssReference extends CssResourceReference {
    private static final long serialVersionUID = 1L;

    private static final TabulatorCssReference INSTANCE =
        new TabulatorCssReference();

    private static final WebjarsCssResourceReference WEBJARS_INSTANCE =
    		new WebjarsCssResourceReference("tabulator-tables/6.3.1/dist/css/tabulator.min.css");

    
    public static WebjarsCssResourceReference getWebjars() {
        return WEBJARS_INSTANCE;
    }

    public static TabulatorCssReference get() {
        return INSTANCE;
    }

    private TabulatorCssReference() {
        super(TabulatorCssReference.class,
              "/webjars/tabulator-tables/6.3.1/dist/css/tabulator.min.css");
    }
}
