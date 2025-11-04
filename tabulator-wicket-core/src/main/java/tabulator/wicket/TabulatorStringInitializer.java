package tabulator.wicket;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;

public class TabulatorStringInitializer implements ITabulatorInitializer {
	private final String js;

	public TabulatorStringInitializer(String js) {
		this.js = js;
	}

	@Override
	public String generateScript(Component component, String tableVarName) {
		
		return StringUtils.replaceEach(js
			, new String[] {
				"${markupId}"
				,"${tableVarName}"
				}
			, new String[] {
				component.getMarkupId()
				,tableVarName
				}
		);
		
	}
}