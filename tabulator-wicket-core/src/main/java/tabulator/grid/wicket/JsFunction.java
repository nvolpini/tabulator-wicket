package tabulator.grid.wicket;

import java.io.Serializable;
import java.util.UUID;

public class JsFunction implements Serializable {

	private final String source;
	private final String id;

	private JsFunction(String source) {
		this.source = source;
		this.id = "__JSFN_" + UUID.randomUUID().toString().replace("-", "") + "__";
	}

	public static JsFunction of(String source) {
		return new JsFunction(source);
	}

	public String getSource() {
		return source;
	}

	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return source;
	}
}