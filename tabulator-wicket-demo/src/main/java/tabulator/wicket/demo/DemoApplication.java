package tabulator.wicket.demo;

import org.apache.wicket.Page;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;

import tabulator.wicket.ITabulatorSettings;
import tabulator.wicket.TabulatorSettings;
import tabulator.wicket.TabulatorWicketPlugin;

public class DemoApplication extends WebApplication {
	
    @Override
    public Class<? extends Page> getHomePage() {
        return DemoHomePage.class;
    }

    @Override
    public void init() {
        super.init();
        
        ITabulatorSettings settings = new TabulatorSettings();
        //settings.setUseCdn(true);
        //settings.setCdnJs("https://unpkg.com/tabulator-tables@6.3.1/dist/js/tabulator.min.js");
        //settings.setCdnCss("https://unpkg.com/tabulator-tables@6.3.1/dist/css/tabulator.min.css");
        settings.setUseCdn(false);
        	
		TabulatorWicketPlugin.install(this, settings);
        
        mountPage("/", DemoHomePage.class);
        

        // Monta o recurso REST JSON
        mountResource("/api/demo1", new ResourceReference("demo1") {
            private static final long serialVersionUID = 1L;
            @Override
            public IResource getResource() {
                return new DemoDataResource();
            }
        });
    }
    
    @Override
    public RuntimeConfigurationType getConfigurationType() {
    	return super.getConfigurationType();
    }
}
