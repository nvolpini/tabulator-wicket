package tabulator.wicket.resources;

import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

import de.agilecoders.wicket.webjars.request.resource.WebjarsCssResourceReference;
import tabulator.wicket.TabulatorTheme;

public class TabulatorThemeReference extends CssResourceReference {
    private static final long serialVersionUID = 1L;

    private TabulatorThemeReference() {
        super(TabulatorThemeReference.class,
              "/webjars/tabulator-tables/6.3.1/dist/css/tabulator.min.css");
    }

	public static WebjarsCssResourceReference forTheme(TabulatorTheme theme) {
		return new WebjarsCssResourceReference(String.format("tabulator-tables/6.3.1/dist/css/%s", theme.getCssFile()));
	}
}
