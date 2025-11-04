package tabulator.wicket;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tabulator.wicket.behavior.AbstractTabulatorAjaxBehavior;
import tabulator.wicket.resources.TabulatorCssReference;
import tabulator.wicket.resources.TabulatorJsReference;

public class TabulatorBehavior extends Behavior {

    private static final Logger log = LoggerFactory.getLogger(TabulatorBehavior.class);

    private Component boundComponent;

    private final ITabulatorInitializer initializer;

    private final List<AbstractTabulatorAjaxBehavior> behaviors = new ArrayList<>();
    
    

    private String tableVarName;
    
    public TabulatorBehavior(ITabulatorInitializer initializer) {
        
        this.initializer = initializer;
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

    @Override
    public void unbind(Component component) {
        super.unbind(component);
        this.boundComponent = null;
    }

    @Override
    public final void renderHead(Component c, IHeaderResponse r){
    	
        log.debug("RenderHead for component: {}", c.getMarkupId());

        renderHeadResources(c, r);
        
        // script principal
        String tableVar = getTableVarName();
        String initJs = initializer.generateScript(c, tableVar);

        StringBuilder js = new StringBuilder(initJs);

        for (AbstractTabulatorAjaxBehavior ev : behaviors) {
        	js.append(ev.createEventScript(tableVar));
        }
        
        r.render(OnDomReadyHeaderItem.forScript(js.toString()));
        
        onRenderHead(c, r);
    }
    
    protected void onRenderHead(Component c, IHeaderResponse r) {
		
	}

	/**
     * Renders head resources (JS and CSS) according to ITabulatorSettings.
     * <br>Is it possible to override and provide custom resources
     * @param c
     * @param r
     */
    protected void renderHeadResources(Component c, IHeaderResponse r) {

        ITabulatorSettings settings = TabulatorWicketPlugin.settings();
		
		if(settings.isUseCdn()){
		    r.render(CssHeaderItem.forUrl(settings.getCdnCss()));
		    r.render(JavaScriptHeaderItem.forUrl(settings.getCdnJs()));
		} else {
			  r.render(CssHeaderItem.forReference(TabulatorCssReference.getWebjars()));
		      r.render(JavaScriptHeaderItem.forReference(TabulatorJsReference.getWebjars()));
		}
		/**
		  r.render(CssHeaderItem.forReference(TabulatorCssReference.getWebjars()));
	      r.render(JavaScriptHeaderItem.forReference(TabulatorJsReference.getWebjars()));
	**/
    }

    public final String getTableVarName() {
    	return tableVarName;
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
}