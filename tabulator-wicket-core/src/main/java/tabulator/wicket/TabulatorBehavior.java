package tabulator.wicket;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.model.IDetachable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import tabulator.wicket.behavior.AbstractTabulatorAjaxBehavior;
import tabulator.wicket.resources.LuxonResourceReference;
import tabulator.wicket.resources.TabulatorCssReference;
import tabulator.wicket.resources.TabulatorJsReference;
import tabulator.wicket.resources.TabulatorThemeReference;

public class TabulatorBehavior extends Behavior {

    private static final Logger log = LoggerFactory.getLogger(TabulatorBehavior.class);

    private Component boundComponent;

    private final ITabulatorInitializer initializer;

    private TabulatorInitializerValidator.ValidationMode validationMode =
            TabulatorInitializerValidator.ValidationMode.STRICT;

    private final List<AbstractTabulatorAjaxBehavior> behaviors = new ArrayList<>();
    
    private TabulatorTheme themeOverride;

    private Boolean luxonEnabledOverride;
    
    private String luxonCdnUrlOverride;

    private String tableVarName;

    private String locale;
    
    private boolean useDefaults = true;
    
    private final TabulatorDefaultOptions options = new TabulatorDefaultOptions();

    public TabulatorBehavior(ITabulatorInitializer initializer) {
        this.initializer = Objects.requireNonNull(initializer);

    }

    public TabulatorBehavior add(AbstractTabulatorAjaxBehavior event) {
    	behaviors.add(event);


        if (boundComponent != null) {
            boundComponent.add(event);
        }
        
        return this;
    }


    @Override
    public void bind(Component component) {
        super.bind(component);

        this.boundComponent = component;

        this.tableVarName = "tab_" + component.getMarkupId();
        
        log.debug("bind for component: {}, tableVarName: {}"
        		, component.getMarkupId(), tableVarName);
        

        for (AbstractTabulatorAjaxBehavior ev : behaviors) {
        	if (!ev.isBound()) { //evita adicionar N vezes
                component.add(ev);
            }
        }
        

    }

    private void mergeDefaultOptions() {
        ObjectNode defaults = TabulatorWicketPlugin.settings()
                .getDefaultOptions().asJson();

        defaults.fields().forEachRemaining(e -> {
            if (!options.asJson().has(e.getKey())) {
                options.asJson().set(e.getKey(), e.getValue());
            }
        });
    }

    private void injectTranslations() {
        getLocale().ifPresent(lang->{
        	TabulatorWicketPlugin.settings().getTranslation(lang).ifPresent(json -> {
        		options.set("locale", true);
        		
        		if (!options.asJson().has("langs")) {
                    options.asJson().set("langs", json);
                } else {
                    // merge adicional (caso já exista)
                    ((ObjectNode) options.asJson().get("langs")).setAll((ObjectNode) json);
                }
                options.set("lang", lang); //TODO isso nao existe no tabulator
                
            });
        });
        
    }
    
    
    @Override
    public void unbind(Component component) {
        super.unbind(component);
        this.boundComponent = null;
    }
    
    @Override
    public void detach(Component component) {
    	super.detach(component);
    	
    	if (initializer instanceof IDetachable) {
    		((IDetachable) initializer).detach();
    	}
    	
    }
    
    @Override
    public final void renderHead(Component c, IHeaderResponse r){
    	
        log.debug("RenderHead for component: {}", c.getMarkupId());

        // script principal
        String tableVar = getTableVarName();

        prepareFinalOptions();
        
        // Renders the template - resolve as variáveis
        String renderedTemplate = initializer.generateScript(c, getTableVarName());


        var validator = new TabulatorInitializerValidator(validationMode);
        
        // Extract json options from the parsed template
        ObjectNode templateOptions = validator.extractAndParseFromRenderedString(renderedTemplate);

        
        //ObjectNode templateOptions = validator.validateAndExtract(c, initializer);

        
        // merge (template options + settings defaults + behavior/local options)
        ObjectNode merged = mergeOptions(templateOptions, getFinalOptions());

        String finalScript = replaceTabulatorOptions(renderedTemplate, merged.toPrettyString());

        
        // add the events
        for (AbstractTabulatorAjaxBehavior ev : behaviors) {
        	finalScript += ev.createEventScript(tableVar);
        }

        r.render(OnDomReadyHeaderItem.forScript(finalScript));
        
        //renders all the head resources
        renderHeadResources(c, r);
        
        
        
		/*
		String initJs = initializer.generateScript(c, tableVar);
		
		StringBuilder js = new StringBuilder(initJs);
		
		for (AbstractTabulatorAjaxBehavior ev : behaviors) {
			js.append(ev.createEventScript(tableVar));
		}
		
		r.render(OnDomReadyHeaderItem.forScript(js.toString()));
		*/
        
        //callback
        onRenderHead(c, r);
    }
    
    public String replaceTabulatorOptions(String script, String jsonOptions) {
        int start = script.indexOf('{');
        int end = script.lastIndexOf('}');
        if (start < 0 || end <= start) return script;

        String before = script.substring(0, start);
        String after = script.substring(end + 1);

        return before + jsonOptions + after;
    }
    
    public ObjectNode mergeOptions(ObjectNode base, ObjectNode override) {
        ObjectNode merged = base.deepCopy();

        override.fields().forEachRemaining(e -> {
            String key = e.getKey();
            var value = e.getValue();

            if (merged.has(key) && merged.get(key).isObject() && value.isObject()) {
                // merge recursivo para objetos internos
                ObjectNode mergedChild = mergeOptions((ObjectNode) merged.get(key), (ObjectNode) value);
                merged.set(key, mergedChild);
            } else {
                // substitui valor direto
                merged.set(key, value);
            }
        });

        return merged;
    }


    private void prepareFinalOptions() {
	    if (useDefaults && TabulatorWicketPlugin.settings().isApplyDefaultOptions()) {
	        mergeDefaultOptions();
	    }
	    injectTranslations();
	}

    public ObjectNode getFinalOptions() {
        // inclui defaults, locale e opções locais
        ObjectNode node = options.asJson();
        // merge com settings globais e tradução
        // (mesmo código que já temos implementado)
        return node;
    }
    protected void onRenderHead(Component c, IHeaderResponse r) {
		
	}

	/**
     * Renders head resources (JS and CSS) according to ITabulatorSettings.
     * <br>The add 'extra' resources, use {@link #onRenderHead(Component, IHeaderResponse)}
     * @param c
     * @param r
     */
    protected void renderHeadResources(Component c, IHeaderResponse r) {

        ITabulatorSettings settings = TabulatorWicketPlugin.settings();
        
        TabulatorTheme theme = themeOverride != null ? themeOverride : settings.theme();

        
		if(settings.isUseCdn()){
		    r.render(CssHeaderItem.forUrl(settings.getCdnCss()));
		    r.render(JavaScriptHeaderItem.forUrl(settings.getCdnJs()));
		    Optional.ofNullable(settings.getCdnThemeCss()).ifPresent(
		    		css->r.render(CssHeaderItem.forUrl(css))
		    		);
		    
		} else {
			  r.render(CssHeaderItem.forReference(TabulatorCssReference.getWebjars()));
		      r.render(JavaScriptHeaderItem.forReference(TabulatorJsReference.getWebjars()));
		      Optional.ofNullable(theme).ifPresent(t->r.render(CssHeaderItem.forReference(TabulatorThemeReference.forTheme(t))));
		      
		}
		
        boolean enabled = (luxonEnabledOverride != null) ? luxonEnabledOverride : settings.isLuxonEnabled();
        if (enabled) {
            String cdnUrl = (luxonCdnUrlOverride != null) ? luxonCdnUrlOverride : 
                             (settings.isUseLuxonCdn() ? settings.getLuxonCdnUrl() : null);

            if (cdnUrl != null) {
                r.render(JavaScriptHeaderItem.forUrl(cdnUrl, "luxon-cdn"));
            } else {
                r.render(JavaScriptHeaderItem.forReference(LuxonResourceReference.get()));
            }
        }

		
		
		/**
		  r.render(CssHeaderItem.forReference(TabulatorCssReference.getWebjars()));
	      r.render(JavaScriptHeaderItem.forReference(TabulatorJsReference.getWebjars()));
	**/
    }

    public final String getTableVarName() {
    	return tableVarName;
    }

	public TabulatorBehavior theme(TabulatorTheme theme) {
	    this.themeOverride = theme;
	    return this;
	}

    public void reload(AjaxRequestTarget target) {
    	
    	TabulatorUtils.runOnTable(target, boundComponent, (tableVar) -> String.format("%s.setData();", tableVar));
    	
    }

    public void setData(AjaxRequestTarget target, String url) {
    	
    	TabulatorUtils.runOnTable(target, boundComponent, (tableVar) -> String.format("%s.setData('%s');", tableVar, url));
    }

    public void clearData(AjaxRequestTarget target) {
    	
    	TabulatorUtils.runOnTable(target, boundComponent, (tableVar) -> String.format("%s.clearData();", tableVar));
    	
    }

    public void runOnTable(AjaxRequestTarget target, String js) {
    	
    	TabulatorUtils.runOnTable(target, boundComponent, (tableVar) -> String.format("%s.%s", tableVar, js));
    	
    }

    public TabulatorBehavior useLuxon(boolean enabled) {
        this.luxonEnabledOverride = enabled;
        return this;
    }
    public TabulatorBehavior useLuxonCdn(String url) {
        this.luxonCdnUrlOverride = url;
        return this;
    }
    

    public TabulatorBehavior disableDefaults() {
        this.useDefaults = false;
        return this;
    }

    public TabulatorDefaultOptions options() {
        return options;
    }
    

    public TabulatorBehavior setLocale(String locale) {
        this.locale = locale;
        return this;
    }

    public Optional<String> getLocale() {
        return locale != null ? Optional.ofNullable(locale) : TabulatorWicketPlugin.settings().getDefaultLocale();
    }

    public TabulatorBehavior validationMode(TabulatorInitializerValidator.ValidationMode mode) {
        this.validationMode = mode;
        return this;
    }
}