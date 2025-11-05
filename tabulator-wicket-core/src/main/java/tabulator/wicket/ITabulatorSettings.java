package tabulator.wicket;

import de.agilecoders.wicket.webjars.settings.WebjarsSettings;
import tabulator.wicket.json.ObjectMapperFactory;

public interface ITabulatorSettings {

	ObjectMapperFactory getObjectMapperFactory();

	void setUseCdn(boolean useCdn);

	boolean isUseCdn();
	
		void setCdnCss(String cdnCss);
	
		String getCdnCss();
	
		void setCdnJs(String cdnJs);
	
		String getCdnJs();

	WebjarsSettings getWebjarsSettings();

	void setWebjarsSettings(WebjarsSettings webjarsSettings);
	/*
	void setCdnBaseUrl(String cdnBaseUrl);
	
	String getCdnBaseUrl();*/

	 TabulatorTheme theme();
	 ITabulatorSettings theme(TabulatorTheme theme);

	 void setCdnThemeCss(String cdnThemeCss);

	 String getCdnThemeCss();
}
