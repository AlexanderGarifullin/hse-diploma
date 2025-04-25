package hse.diploma.enums.props.values;

/**
 * Порядок сортировки элементов в массиве или строке.
 */
public enum SortedOrder {
    /**
     * Порядок не важен.
     */
    NONE("none"),

    /**
     * Элементы отсортированы по возрастанию.
     */
    ASC("asc"),

    /**
     * Элементы отсортированы по убыванию.
     */
    DESC("desc");

    private final String key;

    SortedOrder(String key) {
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
