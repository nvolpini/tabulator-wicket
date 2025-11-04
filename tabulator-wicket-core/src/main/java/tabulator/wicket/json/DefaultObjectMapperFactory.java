package tabulator.wicket.json;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DefaultObjectMapperFactory implements ObjectMapperFactory {

	@Override
	public ObjectMapper newObjectMapper() {
        return configure(new ObjectMapper());

	}

    /**
     * configures given object mapper instance.
     *
     * @param mapper the object to configure
     * @return mapper instance for chaining
     */
    protected ObjectMapper configure(ObjectMapper mapper) {
       
        return mapper;
    }
}
