package hse.diploma.utils.map;

import java.util.HashMap;
import java.util.Map;

public class PropMapBuilder {
    private final Map<String, Object> map = new HashMap<>();

    private PropMapBuilder() {}

    public static PropMapBuilder create() {
        return new PropMapBuilder();
    }

    public PropMapBuilder put(String key, Object value) {
        map.put(key, value);
        return this;
    }

    public Map<String, Object> build() {
        return map;
    }
}
