package tabulator.wicket.demo;

import org.apache.wicket.protocol.http.WicketFilter;

import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.annotation.WebInitParam;

@WebFilter(value = "/*", initParams = {
        @WebInitParam(name = "applicationClassName", value = "tabulator.wicket.demo.DemoApplication"),
		@WebInitParam(name="filterMappingUrlPattern", value="/*") })
public class DemoWicketFilter extends WicketFilter {


}
