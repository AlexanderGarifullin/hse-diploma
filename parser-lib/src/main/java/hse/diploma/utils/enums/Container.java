package hse.diploma.utils.enums;

/**
 * Контейнер, определяющий структуру хранения переменной.
 */
public enum Container {
    /**
     * Простая переменная (одно значение).
     */
    SCALAR("scalar"),

    /**
     * Одномерный массив.
     */
    ARRAY("array"),

    /**
     * Двумерный массив (матрица).
     * Может использоваться для массивов строк или чисел.
     */
    MATRIX("matrix"),

    /**
     * Объект из нескольких вложенных переменных.
     * Например, строка вида: "n m k" → OBJECT с полями n, m, k.
     */
    OBJECT("object");

    private final String key;

    Container(String key) {
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
