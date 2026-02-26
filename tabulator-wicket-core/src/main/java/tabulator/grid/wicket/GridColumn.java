package tabulator.grid.wicket;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class GridColumn {

	private final Map<String, Object> data = new LinkedHashMap<>();

	private GridColumn() {
	}

	private GridColumn(String title, String field) {
		data.put("title", title);
		data.put("field", field);
	}

	public static GridColumn of(String title, String field) {
		return new GridColumn(title, field);
	}

	public GridColumn field(String field) {
		data.put("field", field);
		return this;
	}
	
	public GridColumn headerTooltip(String tooltip) {
		data.put("headerTooltip", tooltip);
		return this;
	}

	public GridColumn widthGrow(int v) {
		data.put("widthGrow", v);
		return this;
	}

	public GridColumn resizable(boolean v) {
		data.put("resizable", v);
		return this;
	}

	public GridColumn frozen(boolean v) {
		data.put("frozen", v);
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

	public GridColumn headerFilterParams(Map<String, Object> v) {
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

	public GridColumn formatterParams(Map<String, Object> v) {
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

	public Map<String, Object> build() {
		return data;
	}

	public String getField() {

		Object fo = data.get("field");

		if (fo == null) {
			return null;
		}

		if (fo instanceof String f) {
			return f;

		}

		return fo.toString();
	}

	public GridColumn align(String value) {
		return option("hozAlign", value);
	}

	public GridColumn headerAlign(String value) {
		return option("headerHozAlign", value);
	}

	public GridColumn left() {
		return align("left");
	}

	public GridColumn center() {
		return align("center");
	}

	public GridColumn right() {
		return align("right");
	}
	
	public GridColumn headerLeft() {
		return headerAlign("left");
	}

	public GridColumn headerCenter() {
		return headerAlign("center");
	}

	public GridColumn headerRight() {
		return headerAlign("right");
	}
	
	public GridColumn money() {
		return formatter("money");
	}

	public GridColumn tickCross() {
		return formatter("tickCross");
	}

	public GridColumn datetime(String inputFormat, String outputFormat) {
		formatter("datetime");
		formatterParams(Map.of("inputFormat", inputFormat, "outputFormat", outputFormat));
		return this;
	}
	public GridColumn headerFilterInput() {
	    return headerFilter("input");
	}

	public GridColumn headerFilterNumber() {
	    return headerFilter("number");
	}

	
	public GridColumn width(int px) {
	    return option("width", px);
	}

	public GridColumn minWidth(int px) {
	    return option("minWidth", px);
	}
	
	public GridColumn headerSort(boolean v) {
	    return option("headerSort", v);
	}
	
	public GridColumn noSort() {
	    return headerSort(false);
	}
	
	public GridColumn js(String key, JsFunction fn) {
	    return option(key, fn);
	}
	public GridColumn formatterJs(JsFunction fn) {
	    return js("formatter", fn);
	}

	public GridColumn cellClick(JsFunction fn) {
	    return js("cellClick", fn);
	}

	public GridColumn headerFilterPlaceholder(String p) {
	    return option("headerFilterPlaceholder", p);
	}
	
	
	public GridColumn headerFilterFunc(JsFunction fn) {
	    return js("headerFilterFunc", fn);
	}
	

	public GridColumn headerFilterFuncName(String fn) {
	    return option("headerFilterFunc", fn);
	}
	
	public GridColumn mutator(JsFunction fn) {
	    return js("mutator", fn);
	}

	public GridColumn headerFilterList(Map<String,Object> values) {
	    headerFilter("list");
	    headerFilterParams(Map.of("values", values));
	    return this;
	}
	
	public GridColumn headerFilterList(Consumer<HeaderFilterListBuilder> consumer) {

	    HeaderFilterListBuilder builder = new HeaderFilterListBuilder();
	    consumer.accept(builder);

	    headerFilter("list");
	    headerFilterParams(builder.build());

	    return this;
	}
	
	public GridColumn headerFilterList(
	        Map<String,Object> values,
	        Consumer<HeaderFilterListBuilder> consumer) {

	    HeaderFilterListBuilder builder = new HeaderFilterListBuilder();
	    builder.values(values);

	    if (consumer != null) {
	        consumer.accept(builder);
	    }

	    headerFilter("list");
	    headerFilterParams(builder.build());

	    return this;
	}
	
	public GridColumn sort(Consumer<SortBuilder> consumer) {

	    SortBuilder builder = new SortBuilder();
	    consumer.accept(builder);

	    if (builder.getSorter() != null) {
	        option("sorter", builder.getSorter());
	    }

	    if (!builder.getParams().isEmpty()) {
	        option("sorterParams", builder.getParams());
	    }

	    headerSort(true); // se chamou sort, assume que quer sortable

	    return this;
	}
	
	public GridColumn sortNumber() {
	    return sort(s -> s.type("number"));
	}

	public GridColumn sortString() {
	    return sort(s -> s.type("string"));
	}

	public GridColumn sortDate(String format) {
	    return sort(s -> s
	        .type("date")
	        .param("format", format)
	    );
	}
	
	public GridColumn sortJs(JsFunction fn) {
	    option("sorter", fn);
	    headerSort(true);
	    return this;
	}
	
	public static class HeaderFilterListBuilder {

	    private final Map<String,Object> params = new LinkedHashMap<>();

	    public HeaderFilterListBuilder values(Map<String,Object> values) {
	        params.put("values", values);
	        return this;
	    }

	    public HeaderFilterListBuilder valuesLookup(boolean v) {
	        params.put("valuesLookup", v);
	        return this;
	    }

	    public HeaderFilterListBuilder emptyValue(Object v) {
	        params.put("emptyValue", v);
	        return this;
	    }

	    public HeaderFilterListBuilder clearable(boolean v) {
	        params.put("clearable", v);
	        return this;
	    }

	    public HeaderFilterListBuilder autocomplete(boolean v) {
	        params.put("autocomplete", v);
	        return this;
	    }

	    Map<String,Object> build() {
	        return params;
	    }
	}
	
	public static class SortBuilder {

	    private String sorter;
	    private final Map<String,Object> params = new LinkedHashMap<>();

	    public SortBuilder type(String sorter) {
	        this.sorter = sorter;
	        return this;
	    }

	    public SortBuilder param(String key, Object value) {
	        params.put(key, value);
	        return this;
	    }

	    public SortBuilder params(Map<String,Object> map) {
	        params.putAll(map);
	        return this;
	    }

	    String getSorter() {
	        return sorter;
	    }

	    Map<String,Object> getParams() {
	        return params;
	    }
	}
}
