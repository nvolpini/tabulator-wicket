package tabulator.wicket;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;

public class TabulatorUtils {
    
	/**
	 * Retorna o codigo para buscar a tabela do tabulator:
	 * <pre>
	 * Tabulator.findTable('#" + tableComponent.getMarkupId() + "')[0]
	 * </pre>
	 * @param tableComponent
	 * @return
	 */
	public static String findTableJavascript(Component tableComponent) {
		return "Tabulator.findTable('#" + tableComponent.getMarkupId() + "')[0]";
	}
	
	public static void runOnTable(AjaxRequestTarget target, Component tableComponent, TableFunction fn) {
        String js = fn.getFunctionBody(findTableJavascript(tableComponent));
        target.appendJavaScript(js);
    }
    
    public static void runOnTableReplaceData(AjaxRequestTarget target, Component tableComponent) {
    	TabulatorUtils.runOnTableReplaceData(target, tableComponent, null);
    }
    
    public static void runOnTableReplaceData(AjaxRequestTarget target, Component tableComponent, String url) {
    	TabulatorUtils.runOnTable(target, tableComponent, new TableFunction() {
    	    @Override
    	    public String getFunctionBody(String tableVar) {
    	        return String.format("%s.replaceData(%s);", tableVar, (url != null ? StringUtils.wrap(url, "'") : ""));
    	    }
    	});
    }
    
}