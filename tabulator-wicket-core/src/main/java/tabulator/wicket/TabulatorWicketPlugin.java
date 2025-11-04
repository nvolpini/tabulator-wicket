package tabulator.wicket;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import org.apache.wicket.Application;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.protocol.http.WebApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import de.agilecoders.wicket.webjars.WicketWebjars;
import de.agilecoders.wicket.webjars.settings.WebjarsSettings;
import tabulator.wicket.lang.Translations;

public class TabulatorWicketPlugin {
	
	private static final Logger log = LoggerFactory.getLogger(TabulatorWicketPlugin.class);
	/**
	 * The {@link org.apache.wicket.MetaDataKey} used to retrieve the
	 * {@link TabulatorWicketPlugin} from the Wicket {@link Appendable}.
	 */
	private static final MetaDataKey<ITabulatorSettings> TABULATOR_WICKET_SETTINGS_PLUGIN_METADATA_KEY = new MetaDataKey<ITabulatorSettings>() {
	};

	/**
	 * private constructor.
	 */
	private TabulatorWicketPlugin() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Checks whether this library support is already installed
	 *
	 * @param application the wicket application
	 * @return {@code true} if library is already installed, otherwise {@code false}
	 */
	public static boolean isInstalled(Application application) {
		return application != null && application.getMetaData(TABULATOR_WICKET_SETTINGS_PLUGIN_METADATA_KEY) != null;
	}

	/**
	 * installs the library to given application
	 *
	 * @param app the wicket application
	 */
	public static void install(final Application app) {
		install(app, null);
	}
	/**
	 * installs the library settings to given app.
	 *
	 * @param app      the wicket application
	 * @param settings the settings to use
	 */
	public static void install(Application app, ITabulatorSettings settings) {
		final ITabulatorSettings existingSettings = settings(app);

		if (existingSettings == null) {
			if (settings == null) {
				settings = new TabulatorSettings();
			}
			
			
			app.setMetaData(TABULATOR_WICKET_SETTINGS_PLUGIN_METADATA_KEY, settings);

	        WicketWebjars.install((WebApplication)app, settings.getWebjarsSettings());

			loadTranslation(settings);

			log.info("initialize TabulatorWicketPlugin with given settings: {}", settings);
		}
	}

	/**
	 * returns the {@link ITabulatorSettings} which are assigned to given
	 * application
	 *
	 * @param app The current application
	 * @return assigned {@link ITabulatorSettings}
	 */
	public static ITabulatorSettings settings(final Application app) {
		return app.getMetaData(TABULATOR_WICKET_SETTINGS_PLUGIN_METADATA_KEY);
	}

	/**
	 * returns the {@link ITabulatorSettings} which are assigned to given
	 * application or a new default instance.
	 *
	 * This is an internal API method, please don't use it.
	 *
	 * @return assigned {@link ITabulatorSettings}
	 */
	public static ITabulatorSettings assignedSettingsOrDefault() {
		Application app = Application.exists() ? Application.get() : null;

		if (isInstalled(app)) {
			return settings();
		} else {
			log.info(
					"try to get settings, but TabulatorWicketPlugin wasn't installed to current application. Fallback to default settings.");

			return new TabulatorSettings();
		}
	}

	/**
	 * returns the {@link ITabulatorSettings} which are assigned to
	 * current application
	 *
	 * @return assigned {@link ITabulatorSettings}
	 */
	public static ITabulatorSettings settings() {
		if (Application.exists()) {
			final ITabulatorSettings settings = Application.get()
					.getMetaData(TABULATOR_WICKET_SETTINGS_PLUGIN_METADATA_KEY);

			if (settings != null) {
				return settings;
			} else {
				throw new IllegalStateException("you have to call TabulatorWicketPlugin.install()");
			}
		}

		throw new IllegalStateException("there is no active application assigned to this thread.");
	}

	public static Optional<JsonNode> getTranslation(String lang) {

		final String fileName = "tabulator/wicket/lang/"+lang + ".json";
		
		ClassLoader classLoader = Translations.class.getClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream(fileName);

		if (inputStream == null) {
			log.error("No translation file found: {}", fileName);
			return Optional.empty();
		}

		ObjectMapper mapper = new ObjectMapper();

		JsonNode rootNode;

		try {

			rootNode = mapper.readValue(inputStream, JsonNode.class);

			ObjectNode langNode = mapper.createObjectNode();
			langNode.set(lang, rootNode);

			//log.trace("json: {}", mapper.writeValueAsString(langNode));

			log.debug("translation '{}' loaded from: {}", lang, fileName);

			return Optional.ofNullable(langNode);
			
		} catch (Exception e) {
			log.error("Error loading translation file", e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					log.error("Error loading translation inputstream", e);
				}
			}
		}
		return Optional.empty();
	}
	
	private static void loadTranslation(ITabulatorSettings settings) {
		/*
		settings.getTranslation().ifPresent(lang -> {
			
			getTranslation(lang).ifPresent(node->{
				settings.getDefaultOptions().putCustom("langs", node);
			});
			
			
			
			
		});*/
		

	}
}
