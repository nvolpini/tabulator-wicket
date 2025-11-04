package tabulator.wicket;

import java.util.Optional;

import de.agilecoders.wicket.webjars.settings.WebjarsSettings;
import tabulator.wicket.json.ObjectMapperFactory;
import tabulator.wicket.json.SingletonObjectMapperFactory;

/**
 * Configurações globais do componente Tabulator. Permite customizar CDN, JSON
 * mapper e serialização.
 */
public class TabulatorSettings implements ITabulatorSettings {

	private ObjectMapperFactory objectMapperFactory;
	
	String tranlationLang;
	
	private String cdnJs = "https://unpkg.com/tabulator-tables@6.3.1/dist/js/tabulator.min.js";
	private String cdnCss = "https://unpkg.com/tabulator-tables@6.3.1/dist/css/tabulator.min.css";

	WebjarsSettings webjarsSettings = new WebjarsSettings();

	private boolean useCdn;
	
	public TabulatorSettings() {
		this.objectMapperFactory = new SingletonObjectMapperFactory();
		webjarsSettings.useCdnResources(false);
	}
	
	public ITabulatorSettings setTranslation(String locale) {
		this.tranlationLang = locale;
		return this;
	}
	
	
	public Optional<String> getTranslation() {
		return Optional.ofNullable(tranlationLang);
	}
	
	// ------------------------------------------------------------
	// CDN
	// ------------------------------------------------------------
	@Override
	public boolean isUseCdn() {
		return this.useCdn; 
		//return webjarsSettings.useCdnResources();
	}

	@Override
	public void setUseCdn(boolean useCdn) {
		//webjarsSettings.useCdnResources(useCdn);
		this.useCdn = useCdn;
	}

	
	public String getCdnJs() {
		return cdnJs;
	}

	
	public void setCdnJs(String cdnJs) {
		this.cdnJs = cdnJs;
	}

	
	public String getCdnCss() {
		return cdnCss;
	}

	
	public void setCdnCss(String cdnCss) {
		this.cdnCss = cdnCss;
	}

	@Override
	public ObjectMapperFactory getObjectMapperFactory() {
		return objectMapperFactory;
	}
	
	@Override
	public void setWebjarsSettings(WebjarsSettings webjarsSettings) {
		this.webjarsSettings = webjarsSettings;
	}
	
	@Override
	public WebjarsSettings getWebjarsSettings() {
		return webjarsSettings;
	}

	//@Override
	public String getCdnBaseUrl() {
		return webjarsSettings.cdnUrl();
	}

	//@Override
	public void setCdnBaseUrl(String cdnBaseUrl) {
		webjarsSettings.cdnUrl(cdnBaseUrl);
	}
}
