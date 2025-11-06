package tabulator.wicket;

import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;

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

	void setLuxonCdnUrl(String luxonCdnUrl);

	String getLuxonCdnUrl();

	void setUseLuxonCdn(boolean useLuxonCdn);

	boolean isUseLuxonCdn();

	void setLuxonEnabled(boolean luxonEnabled);

	boolean isLuxonEnabled();

	void setApplyDefaultOptions(boolean applyDefaultOptions);

	boolean isApplyDefaultOptions();

	Optional<String> getDefaultLocale();

	TabulatorDefaultOptions getDefaultOptions();

	Optional<JsonNode> getTranslation(String locale);

	/**
	 * Adiciona ou registra manualmente uma tradução (útil para apps multi-tenant).
	 */
	void registerTranslation(String locale, JsonNode json);

	/** Retorna todas as traduções registradas (cache interno). */
	Map<String, JsonNode> getRegisteredTranslations();

	ITabulatorSettings setDefaultLocale(String locale);

}
