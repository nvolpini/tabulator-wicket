package tabulator.wicket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.io.Serializable;
import java.util.function.Consumer;

public class TabulatorDefaultOptions implements Serializable {

    private static final long serialVersionUID = 1L;

    private transient ObjectMapper mapper;
    private transient ObjectNode root;

    public TabulatorDefaultOptions() {
        initTransientFields();
    }

    private void initTransientFields() {
        if (mapper == null) {
            try {
                mapper = TabulatorWicketPlugin.settings()
                        .getObjectMapperFactory()
                        .newObjectMapper();
            } catch (Exception e) {
                mapper = new ObjectMapper(); // fallback
            }
        }
        if (root == null) {
            root = mapper.createObjectNode();
        }
    }

    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        initTransientFields();
    }

    // region --- Generic setters ---
    public TabulatorDefaultOptions set(String key, String value) {
        asJson().put(key, value);
        return this;
    }

    public TabulatorDefaultOptions set(String key, boolean value) {
        asJson().put(key, value);
        return this;
    }

    public TabulatorDefaultOptions set(String key, int value) {
        asJson().put(key, value);
        return this;
    }

    public TabulatorDefaultOptions set(String key, JsonNode value) {
        asJson().set(key, value);
        return this;
    }

    public TabulatorDefaultOptions set(String key, Object value) {
        asJson().set(key, mapper.valueToTree(value));
        return this;
    }
    // endregion

    // region --- columnDefaults ---
    public TabulatorDefaultOptions addColumnDefault(String key, String value) {
        getOrCreateObject("columnDefaults").put(key, value);
        return this;
    }

    public TabulatorDefaultOptions addColumnDefault(String key, boolean value) {
        getOrCreateObject("columnDefaults").put(key, value);
        return this;
    }

    public TabulatorDefaultOptions addColumnDefault(String key, int value) {
        getOrCreateObject("columnDefaults").put(key, value);
        return this;
    }

    public TabulatorDefaultOptions addColumnDefault(String key, JsonNode value) {
        getOrCreateObject("columnDefaults").set(key, value);
        return this;
    }

    public TabulatorDefaultOptions configureColumnDefaults(Consumer<ObjectNode> consumer) {
        consumer.accept(getOrCreateObject("columnDefaults"));
        return this;
    }

    private ObjectNode getOrCreateObject(String name) {
        JsonNode node = asJson().get(name);
        if (node instanceof ObjectNode obj) return obj;
        ObjectNode created = mapper.createObjectNode();
        asJson().set(name, created);
        return created;
    }
    // endregion

    public ObjectNode asJson() {
        if (root == null) initTransientFields();
        return root;
    }

    public boolean isEmpty() {
        return asJson().isEmpty();
    }

    @Override
    public String toString() {
        return asJson().toPrettyString();
    }
}
