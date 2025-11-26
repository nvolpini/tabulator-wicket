package tabulator.wicket;

public class TabulatorTemplateParts {
	private final String preamble;
	private final String jsonObject;
	private final String postamble;

	public TabulatorTemplateParts(String preamble, String jsonObject, String postamble) {
		super();
		this.preamble = preamble;
		this.jsonObject = jsonObject;
		this.postamble = postamble;
	}

	
	
	public String preamble() {
		return preamble;
	}

	public String jsonObject() {
		return jsonObject;
	}

	public String postamble() {
		return postamble;
	}

}
