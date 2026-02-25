package tabulator.grid.wicket;

public enum SortDir {
	ASC("asc"), DESC("desc");

	private final String js;

	SortDir(String js) {
		this.js = js;
	}

	public String js() {
		return js;
	}
}