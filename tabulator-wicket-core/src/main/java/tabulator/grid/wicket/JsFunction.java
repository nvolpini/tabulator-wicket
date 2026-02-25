package tabulator.grid.wicket;

import java.io.Serializable;

public class JsFunction implements Serializable {

    private final String source;

    private JsFunction(String source) {
        this.source = source;
    }

    public static JsFunction of(String source) {
        return new JsFunction(source);
    }

    public String getSource() {
        return source;
    }

    @Override
    public String toString() {
        return source;
    }
}