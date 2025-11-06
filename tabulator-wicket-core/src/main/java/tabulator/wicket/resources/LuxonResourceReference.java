package tabulator.wicket.resources;

import de.agilecoders.wicket.webjars.request.resource.WebjarsJavaScriptResourceReference;

public class LuxonResourceReference extends WebjarsJavaScriptResourceReference {
	
	private static final LuxonResourceReference INSTANCE = new LuxonResourceReference();

	private LuxonResourceReference() {
		super("luxon/3.7.1/build/global/luxon.js");
	}

	public static LuxonResourceReference get() {
		return INSTANCE;
	}
}
