package hse.diploma.enums.props.values;

/**
 * Тип ввода для массивов и матриц.
 * Определяет, как пользователи вводят данные: в одну строку или в несколько.
 */
public enum LineType {
    /**
     * Все элементы находятся в одной строке.
     * Например: 1 2 3 4 5
     */
    SINGLE("single"),

    /**
     * Каждый элемент или подмассив расположен на отдельной строке.
     * Например:
     * <pre>
     * 1
     * 2
     * 3
     * </pre>
     */
    MULTI("multi");

    private final String key;

    LineType(String key) {
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
