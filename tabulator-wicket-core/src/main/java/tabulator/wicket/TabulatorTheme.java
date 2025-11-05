package tabulator.wicket;

public enum TabulatorTheme {
    DEFAULT("tabulator.min.css"),
    MIDNIGHT("tabulator_midnight.min.css"),
    MODERN("tabulator_modern.min.css"),
    SIMPLE("tabulator_simple.min.css"),
    BOOTSTRAP5("tabulator_bootstrap5.min.css"),
    MATERIALIZE("tabulator_materialize.min.css"),
    SEMANTICUI("tabulator_semanticui.min.css"),
    BULMA("tabulator_bulma.min.css"),
    BOOTSTRAP4("tabulator_bootstrap4.min.css");

    private final String cssFile;

    TabulatorTheme(String cssFile) {
        this.cssFile = cssFile;
    }

    public String getCssFile() {
        return cssFile;
    }
}
