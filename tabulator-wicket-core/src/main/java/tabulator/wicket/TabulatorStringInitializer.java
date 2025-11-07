package tabulator.wicket;

import java.util.Optional;

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
				"__markupId__"
				,"__tableVarName__"
				}
			, new String[] {
				component.getMarkupId()
				,tableVarName
				}
		);
		
	}
	
}