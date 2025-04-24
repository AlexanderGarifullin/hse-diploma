package hse.diploma.enums;

/**
 * Базовый тип данных переменной.
 */
public enum BaseType {
    /**
     * Целое число (32-битное).
     */
    INTEGER("integer"),

    /**
     * Длинное целое число (64-битное).
     */
    LONG("long"),

    /**
     * Строка произвольной длины.
     */
    STRING("string");


    private final String key;

    BaseType(String key) {
        this.key = key;
    }

    public String key() {
        return key;
    }

    @Override
    public String toString() {
        return key;
    }
}
