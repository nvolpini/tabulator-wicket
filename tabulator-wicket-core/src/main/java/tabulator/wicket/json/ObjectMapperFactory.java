package tabulator.wicket.json;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * {@link com.fasterxml.jackson.databind.ObjectMapper} factory
 */
public interface ObjectMapperFactory {
	 /**
     * @return new object mapper instance
     */
    ObjectMapper newObjectMapper();
}
