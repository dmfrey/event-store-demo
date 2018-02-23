package io.pivotal.dmfrey.eventStoreDemo.domain.service.converters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BaseJsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.tuple.Tuple;

import java.util.List;

@Component
public class TupleToJsonStringConverter implements Converter<Tuple, String> {

    private final ObjectMapper mapper;

    public TupleToJsonStringConverter( final ObjectMapper mapper ) {

        this.mapper = mapper;

    }

    @Override
    public String convert( Tuple source ) {
        ObjectNode root = toObjectNode(source);
        String json = null;
        try {
            json = mapper.writeValueAsString(root);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Tuple to string conversion failed", e);
        }
        return json;
    }

    private ObjectNode toObjectNode(Tuple source) {
        ObjectNode root = mapper.createObjectNode();
        for (int i = 0; i < source.size(); i++) {
            Object value = source.getValues().get(i);
            String name = source.getFieldNames().get(i);
            if (value == null) {
                root.putNull(name);
            } else {
                root.putPOJO(name, toNode(value));
            }
        }
        return root;
    }

    private ArrayNode toArrayNode(List<?> source) {
        ArrayNode array = mapper.createArrayNode();
        for (Object value : source) {
            if (value != null) {
                array.add(toNode(value));
            }
        }
        return array;
    }

    private BaseJsonNode toNode(Object value) {
        if (value != null) {
            if (value instanceof Tuple) {
                return toObjectNode((Tuple) value);
            }
            else if (value instanceof List<?>) {
                return toArrayNode((List<?>) value);
            }
            else if (!value.getClass().isPrimitive()) {
                return mapper.getNodeFactory().pojoNode(value);
            }
            else {
                return mapper.valueToTree(value);
            }
        }
        return null;
    }

}
