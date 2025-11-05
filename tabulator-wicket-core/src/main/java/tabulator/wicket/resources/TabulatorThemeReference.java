package tabulator.wicket.resources;

import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

import de.agilecoders.wicket.webjars.request.resource.WebjarsCssResourceReference;
import tabulator.wicket.TabulatorTheme;

public class TabulatorThemeReference extends CssResourceReference {
    private static final long serialVersionUID = 1L;

    private static final TabulatorThemeReference INSTANCE =
        new TabulatorThemeReference();

    private static final WebjarsCssResourceReference WEBJARS_INSTANCE =
    		new WebjarsCssResourceReference("tabulator-tables/6.3.1/dist/css/tabulator.min.css");

    
    public static WebjarsCssResourceReference getWebjars() {
        return WEBJARS_INSTANCE;
    }

    public static TabulatorThemeReference get() {
        return INSTANCE;
    }

    private TabulatorThemeReference() {
        super(TabulatorThemeReference.class,
              "/webjars/tabulator-tables/6.3.1/dist/css/tabulator.min.css");
    }

	public static WebjarsCssResourceReference forTheme(TabulatorTheme theme) {
		return new WebjarsCssResourceReference(String.format("tabulator-tables/6.3.1/dist/css/%s", theme.getCssFile()));
	}
}
