package tabulator.grid.wicket;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GridConfig implements Serializable {

    private String ajaxUrl;
    private String persistenceId;

    private List<Map<String, Object>> columns = new ArrayList<>();
    private Map<String, Object> options = new LinkedHashMap<>();

    public String getAjaxUrl() {
        return ajaxUrl;
    }

    public GridConfig setAjaxUrl(String ajaxUrl) {
        this.ajaxUrl = ajaxUrl;
        return this;
    }

    public String getPersistenceId() {
        return persistenceId;
    }

    public GridConfig setPersistenceId(String persistenceId) {
        this.persistenceId = persistenceId;
        return this;
    }

    public List<Map<String, Object>> getColumns() {
        return columns;
    }

    public GridConfig addColumn(Map<String, Object> column) {
        this.columns.add(column);
        return this;
    }

    public Map<String, Object> getOptions() {
        return options;
    }

    public GridConfig option(String key, Object value) {
        this.options.put(key, value);
        return this;
    }
    
    public static Map<String,Object> map(Object... keyValues) {
        Map<String,Object> m = new LinkedHashMap<>();
        for (int i = 0; i < keyValues.length; i += 2) {
            m.put((String) keyValues[i], keyValues[i + 1]);
        }
        return m;
    }

    public static List<Object> list(Object... values) {
        return Arrays.asList(values);
    }

}
