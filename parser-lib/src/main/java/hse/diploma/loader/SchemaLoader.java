package hse.diploma.loader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hse.diploma.enums.BaseType;
import hse.diploma.enums.Container;
import hse.diploma.enums.props.values.LineType;
import hse.diploma.enums.props.values.SortedOrder;
import hse.diploma.model.Schema;
import hse.diploma.model.VarDescriptor;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Утилита для загрузки Schema из JSON-файла.
 */
public class SchemaLoader {
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Читает JSON из файла и возвращает Schema.
     */
    public Schema load(File file) throws IOException {
        JsonNode root = mapper.readTree(file);
        if (!root.isArray()) {
            throw new IllegalArgumentException("Schema JSON must be an array of VarDescriptor objects");
        }
        List<VarDescriptor> vars = new ArrayList<>();
        for (JsonNode node : root) {
            vars.add(parseVar(node));
        }
        return new Schema(vars);
    }

    private VarDescriptor parseVar(JsonNode node) {
        String name = node.get("name").asText();
        Container container = Container.valueOf(node.get("container").asText().toUpperCase());
        BaseType baseType = BaseType.valueOf(node.get("baseType").asText().toUpperCase());
        JsonNode propsNode = node.get("props");
        Map<String, Object> props = new LinkedHashMap<>();
        if (propsNode != null && propsNode.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> it = propsNode.fields();
            while (it.hasNext()) {
                Map.Entry<String, JsonNode> entry = it.next();
                props.put(entry.getKey(), parsePropValue(entry.getKey(), entry.getValue()));
            }
        }
        return new VarDescriptor(name, container, baseType, props);
    }

    private Object parsePropValue(String key, JsonNode v) {
        if ("fields".equals(key) && v.isArray()) {
            List<VarDescriptor> list = new ArrayList<>();
            for (JsonNode child : v) {
                list.add(parseVar(child));
            }
            return list;
        }
        if (v.isInt() || v.isLong()) {
            return v.asLong();
        }
        if (v.isBoolean()) {
            return v.asBoolean();
        }
        if (v.isTextual()) {
            String text = v.asText();
            if ("sortedOrder".equals(key)) {
                return parseSortedOrder(text);
            }
            if ("lineType".equals(key)) {
                return parseLineType(text);
            }
            return text;
        }
        if (v.isArray()) {
            List<Object> list = new ArrayList<>();
            for (JsonNode e : v) {
                if (e.isTextual()) list.add(e.asText());
                else if (e.isInt() || e.isLong()) list.add(e.asLong());
                else if (e.isBoolean()) list.add(e.asBoolean());
                else list.add(e.toString());
            }
            return list;
        }
        return mapper.convertValue(v, Object.class);
    }
    private String parseSortedOrder(String text) {
        return Arrays.stream(SortedOrder.values())
                .filter(e -> e.key().equalsIgnoreCase(text))
                .map(SortedOrder::key)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown SortedOrder: " + text));
    }

    private String parseLineType(String text) {
        return Arrays.stream(LineType.values())
                .filter(e -> e.key().equalsIgnoreCase(text))
                .map(LineType::key)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown LineType: " + text));
    }

}
