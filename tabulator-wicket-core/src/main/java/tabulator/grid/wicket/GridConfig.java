package tabulator.grid.wicket;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

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
    
    public GridConfig remove(String key) {
        this.options.remove(key);
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
    
    public GridConfig index(String field) {
        options.put("index", field);
        return this;
    }
    
    public GridConfig clearInitialSort() {
        options.remove("initialSort");
        return this;
    }
    
    public GridConfig initialSort(String column, SortDir dir) {

        List<Map<String,Object>> list =
                (List<Map<String,Object>>) options.computeIfAbsent(
                        "initialSort",
                        k -> new ArrayList<>()
                );

        list.add(map(
                "column", column,
                "dir", dir.js()
        ));

        return this;
    }
    
    public GridConfig columnDefaults(Consumer<ColumnDefaults> consumer) {

        ColumnDefaults defaults = new ColumnDefaults();
        consumer.accept(defaults);

        options.put("columnDefaults", defaults.build());

        return this;
    }
    public GridConfig rowFormatter(JsFunction fn) {
        options.put("rowFormatter", fn);
        return this;
    }

    public GridConfig ajaxResponse(JsFunction fn) {
        options.put("ajaxResponse", fn);
        return this;
    }

    public GridConfig ajaxRequestFunc(JsFunction fn) {
        options.put("ajaxRequestFunc", fn);
        return this;
    }
    
    public static class ColumnDefaults implements Serializable {

        private final Map<String,Object> values = new LinkedHashMap<>();

        public ColumnDefaults headerSort(boolean value) {
            values.put("headerSort", value);
            return this;
        }

        public ColumnDefaults center() {
            values.put("hozAlign", "center");
            return this;
        }

        public ColumnDefaults left() {
            values.put("hozAlign", "left");
            return this;
        }

        public ColumnDefaults right() {
            values.put("hozAlign", "right");
            return this;
        }

        Map<String,Object> build() {
            return values;
        }
    }
}
