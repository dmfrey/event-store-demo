package io.pivotal.dmfrey.eventStoreDemo.domain.service.converters;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.tuple.JsonNodeToTupleConverter;
import org.springframework.tuple.Tuple;

@Component
public class JsonStringToTupleConverter implements Converter<byte[], Tuple> {

    private final ObjectMapper mapper;

    private final JsonNodeToTupleConverter jsonNodeToTupleConverter = new JsonNodeToTupleConverter();

    public JsonStringToTupleConverter( ObjectMapper mapper ) {
        this.mapper = mapper;

    }

    @Override
    public Tuple convert(byte[] source) {
        if (source == null) {
            return null;
        }
        try {
            return jsonNodeToTupleConverter.convert( mapper.readTree( source ) );
        }
        catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

}