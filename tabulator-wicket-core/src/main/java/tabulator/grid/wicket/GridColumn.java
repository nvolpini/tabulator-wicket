package tabulator.grid.wicket;

import java.util.LinkedHashMap;
import java.util.Map;

public class GridColumn {

    private final Map<String,Object> data = new LinkedHashMap<>();

    private GridColumn() {}

    private GridColumn(String title, String field) {
        data.put("title", title);
        data.put("field", field);
    }

    public static GridColumn of(String title, String field) {
        return new GridColumn(title, field);
    }

    public GridColumn headerTooltip(String tooltip) {
        data.put("headerTooltip", tooltip);
        return this;
    }
    
    public GridColumn widthGrow(int v) {
        data.put("widthGrow", v);
        return this;
    }

    public GridColumn visible(boolean v) {
        data.put("visible", v);
        return this;
    }

    public GridColumn headerFilter(Object v) {
        data.put("headerFilter", v);
        return this;
    }

    public GridColumn headerFilterParams(Map<String,Object> v) {
        data.put("headerFilterParams", v);
        return this;
    }

    public GridColumn option(String key, Object value) {
        data.put(key, value);
        return this;
    }

    public GridColumn formatter(Object v) {
        return option("formatter", v);
    }

    public GridColumn formatterParams(Map<String,Object> v) {
        return option("formatterParams", v);
    }

    public GridColumn cloneColumn() {
        GridColumn c = new GridColumn();
        c.data.putAll(this.data);
        return c;
    }

    public GridColumn override(java.util.function.Consumer<GridColumn> consumer) {
        GridColumn cloned = this.cloneColumn();
        consumer.accept(cloned);
        return cloned;
    }

    public Map<String,Object> build() {
        return data;
    }
}
