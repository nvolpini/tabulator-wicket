package tabulator.wicket;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import de.agilecoders.wicket.webjars.settings.WebjarsSettings;
import tabulator.wicket.json.ObjectMapperFactory;
import tabulator.wicket.json.SingletonObjectMapperFactory;

/**
 * Configurações globais do componente Tabulator. Permite customizar CDN, JSON
 * mapper e serialização.
 */
public class TabulatorSettings implements ITabulatorSettings {

	private static final Logger log = LoggerFactory.getLogger(TabulatorSettings.class);
	
	private ObjectMapperFactory objectMapperFactory;
	
	String defaultLocale;

	private final Map<String, JsonNode> translations = new ConcurrentHashMap<>();

	TabulatorTheme theme;
	
	private String cdnJs = "https://unpkg.com/tabulator-tables@6.3.1/dist/js/tabulator.min.js";
	private String cdnCss = "https://unpkg.com/tabulator-tables@6.3.1/dist/css/tabulator.min.css";

	private String cdnThemeCss;
	
	WebjarsSettings webjarsSettings = new WebjarsSettings();

	private boolean useCdn;

	private boolean luxonEnabled = true; // padrão
	private boolean useLuxonCdn = false;
	private String luxonCdnUrl = "https://cdn.jsdelivr.net/npm/luxon@3.7.1/build/global/luxon.min.js";

    private final TabulatorDefaultOptions defaultOptions = new TabulatorDefaultOptions();


    private boolean applyDefaultOptions = true;

    private final ObjectMapper mapper = new ObjectMapper();

	public TabulatorSettings() {
		this.objectMapperFactory = new SingletonObjectMapperFactory();
		webjarsSettings.useCdnResources(false);

	}
	

    @Override
    public Optional<JsonNode> getTranslation(String locale) {
        if (translations.containsKey(locale)) {
            return Optional.of(translations.get(locale));
        }

        String file = "tabulator/wicket/lang/" + locale + ".json";
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(file)) {
            if (in == null) {
                log.warn("Translation not found: {}", file);
                return Optional.empty();
            }
            JsonNode json = mapper.readTree(in);
            ObjectNode wrapped = mapper.createObjectNode();
            wrapped.set(locale, json);
            translations.put(locale, wrapped);
            log.debug("Loaded translation for locale: {}", locale);
            return Optional.of(wrapped);
        } catch (IOException e) {
            log.error("Error loading translation {}", locale, e);
            return Optional.empty();
        }
    }

    @Override
    public void registerTranslation(String locale, JsonNode json) {
        translations.put(locale, json);
    }

    @Override
    public Map<String, JsonNode> getRegisteredTranslations() {
        return Collections.unmodifiableMap(translations);
    }

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

	public TabulatorTheme theme() {
		return theme;
	}

	public ITabulatorSettings theme(TabulatorTheme theme) {
		this.theme = theme;
		return this;
	}

	@Override
	public String getCdnThemeCss() {
		return cdnThemeCss;
	}

	@Override
	public void setCdnThemeCss(String cdnThemeCss) {
		this.cdnThemeCss = cdnThemeCss;
	}

	@Override
	public boolean isLuxonEnabled() {
		return luxonEnabled;
	}

	@Override
	public void setLuxonEnabled(boolean luxonEnabled) {
		this.luxonEnabled = luxonEnabled;
	}

	@Override
	public boolean isUseLuxonCdn() {
		return useLuxonCdn;
	}

	@Override
	public void setUseLuxonCdn(boolean useLuxonCdn) {
		this.useLuxonCdn = useLuxonCdn;
	}

	@Override
	public String getLuxonCdnUrl() {
		return luxonCdnUrl;
	}

	@Override
	public void setLuxonCdnUrl(String luxonCdnUrl) {
		this.luxonCdnUrl = luxonCdnUrl;
	}

	@Override
	public ITabulatorSettings setDefaultLocale(String locale) {
		this.defaultLocale = locale;
		return this;
	}
	
	@Override
	public Optional<String> getDefaultLocale() {
		return Optional.ofNullable(defaultLocale);
	}

    @Override
    public TabulatorDefaultOptions getDefaultOptions() {
        return defaultOptions;
    }

	@Override
	public boolean isApplyDefaultOptions() {
		return applyDefaultOptions;
	}


	@Override
	public void setApplyDefaultOptions(boolean applyDefaultOptions) {
		this.applyDefaultOptions = applyDefaultOptions;
	}
}
