package tabulator.wicket.demo;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.wicket.request.resource.AbstractResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Recurso REST simples para devolver dados JSON simulados.
 */
public class DemoDataResource extends AbstractResource {

	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(DemoDataResource.class);

	private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected ResourceResponse newResourceResponse(Attributes attributes) {
        ResourceResponse response = new ResourceResponse();
        response.setContentType("application/json");
        response.setTextEncoding(StandardCharsets.UTF_8.name());
        response.setWriteCallback(new WriteCallback() {
            @Override
            public void writeData(Attributes attributes) {
                try {
                    List<Map<String, Object>> data = generateData();
                    String json = mapper.writeValueAsString(data);
                    attributes.getResponse().write(json);
                } catch (Exception e) {
                    attributes.getResponse().write("{\"error\":\"" + e.getMessage() + "\"}");
                }
            }
        });
        return response;
    }

    private List<Map<String, Object>> generateData() {
        List<Map<String, Object>> list = new ArrayList<>();
        Random rnd = new Random();

        for (int i = 1; i <= 10; i++) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", i);
            row.put("nome", "Produto " + i);
            row.put("valor", rnd.nextInt(1000) / 10.0);
            list.add(row);
        }
        return list;
    }

}
