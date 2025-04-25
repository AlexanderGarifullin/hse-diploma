package hse.diploma.map;

import java.util.HashMap;
import java.util.Map;

/**
 * Построитель map-объектов для props-поля VarDescriptor-а.
 * Упрощает создание вложенных параметров через цепочку .put(...)
 */
public class PropMapBuilder {
    private final Map<String, Object> map = new HashMap<>();

    private PropMapBuilder() {}

    /**
     * Начинает сборку карты свойств.
     */
    public static PropMapBuilder create() {
        return new PropMapBuilder();
    }

    /**
     * Добавляет пару ключ-значение.
     */
    public PropMapBuilder put(String key, Object value) {
        map.put(key, value);
        return this;
    }

    /**
     * Завершает сборку и возвращает готовую Map.
     */
    public Map<String, Object> build() {
        return map;
    }
}
