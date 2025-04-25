package hse.diploma.enums.props;

import hse.diploma.enums.props.values.LineType;
import hse.diploma.enums.props.values.SortedOrder;
import hse.diploma.model.VarDescriptor;

/**
 * Enum, описывающий все допустимые ключи для props в {@link VarDescriptor}.
 */
public enum PropKey {

    /**
     * Минимально допустимое значение переменной.
     * <p>Тип: {@link Long}
     */
    MIN("min"),

    /**
     * Максимально допустимое значение переменной.
     * <p>Тип: {@link Long}
     */
    MAX("max"),

    /**
     * Имя переменной, задающей минимальное значение.
     * <p>Тип: {@link String}, например "n"
     */
    VAR_MIN("varMin"),

    /**
     * Имя переменной, задающей максимальное значение.
     * <p>Тип: {@link String}, например "m"
     */
    VAR_MAX("varMax"),

    /**
     * Список допустимых значений переменной.
     * <p>Тип: {@link java.util.List} (строки или числа)
     */
    ENUM_VALUES("enumValues"),

    /**
     * Максимальная сумма значений переменной по всем наборам входных данных (мультитесты).
     * <p>Тип: {@link Long}
     */
    GLOBAL_SUM_LIMIT("globalSumLimit"),

    /**
     * Максимальное произведение значений переменной по всем наборам входных данных.
     * <p>Тип: {@link Long}
     */
    GLOBAL_PRODUCT_LIMIT("globalProductLimit"),

    /**
     * Все элементы массива или матрицы одинаковы.
     * <p>Тип: {@link Boolean}
     */
    IS_UNIFORM("isUniform"),

    /**
     * Все элементы различны.
     * <p>Тип: {@link Boolean}
     */
    IS_DISTINCT("isDistinct"),

    /**
     * Значения являются перестановкой 1..n.
     * <p>Тип: {@link Boolean}
     */
    IS_PERMUTATION("isPermutation"),

    /**
     * Элементы отсортированы по возрастанию/убыванию.
     * <p>Тип: {@link SortedOrder} — ASC, DESC, NONE
     */
    SORTED_ORDER("sortedOrder"),

    /**
     * Минимальная длина строки или массива.
     * <p>Тип: {@link Integer}
     */
    MIN_LEN("minLen"),

    /**
     * Максимальная длина строки или массива.
     * <p>Тип: {@link Integer}
     */
    MAX_LEN("maxLen"),

    /**
     * Имя переменной, задающей минимальную длину контейнера.
     * <p>Тип: {@link String}
     */
    VAR_MIN_LEN("varMinLen"),

    /**
     * Имя переменной, задающей максимальную длину контейнера.
     * <p>Тип: {@link String}
     */
    VAR_MAX_LEN("varMaxLen"),


    /**
     * Разрешённые символы в строке или массиве символов.
     * <p>Тип: {@link String}, например "01", "abc", "a-zA-Z0-9"
     */
    ALLOWED_CHARS("allowedChars"),

    /**
     * Как организован ввод массива/матрицы.
     * <p>Тип: {@link LineType} — SINGLE или MULTI
     */
    LINE_TYPE("lineType"),

    /**
     * Минимальное количество строк в матрице.
     * <p>Тип: {@link Integer}
     */
    MIN_ROW_COUNT("minRowCount"),

    /**
     * Максимальное количество строк в матрице.
     * <p>Тип: {@link Integer}
     */
    MAX_ROW_COUNT("maxRowCount"),

    /**
     * Имя переменной, задающей число строк в матрице.
     * <p>Тип: {@link String}
     */
    VAR_ROW_COUNT("varRowCount"),

    /**
     * Минимальное количество столбцов в матрице.
     * <p>Тип: {@link Integer}
     */
    MIN_COLUMN_COUNT("minColCount"),

    /**
     * Максимальное количество столбцов в матрице.
     * <p>Тип: {@link Integer}
     */
    MAX_COLUMN_COUNT("maxColCount"),

    /**
     * Имя переменной, задающей число столбцов в матрице.
     * <p>Тип: {@link String}
     */
    VAR_COL_COUNT("varColCount"),

    /**
     * Имя переменной, описывающей тип элемента в контейнере.
     * <p>Тип: {@link String} — ссылается на другую переменную
     */
    ELEMENT_VAR("elementVar"),

    /**
     * Список полей, если переменная — объект (например, строка из нескольких чисел).
     * <p>Тип: {@link java.util.List} из {@link VarDescriptor}
     */
    FIELDS("fields"),

    /**
     * Переменная представляет собой граф.
     * <p>Тип: {@link Boolean} — true, если переменная описывает структуру графа (например, список рёбер).
     */
    IS_GRAPH("isGraph"),

    /**
     * Граф ориентированный.
     * <p>Тип: {@link Boolean} — true, если рёбра направленные.
     */
    IS_DIRECTED("isDirected"),

    /**
     * Граф взвешенный.
     * <p>Тип: {@link Boolean} — true, если у рёбер есть веса.
     */
    IS_WEIGHTED("isWeighted"),

    /**
     * В графе допускаются кратные рёбра (мультиграф).
     * <p>Тип: {@link Boolean}
     */
    IS_MULTIGRAPH("isMultigraph"),

    /**
     * Граф связный.
     * <p>Тип: {@link Boolean}
     */
    IS_CONNECTED("isConnected"),

    /**
     * Граф — дерево.
     * <p>Тип: {@link Boolean} — true, если граф — связное ацикличное дерево.
     */
    IS_TREE("isTree"),

    /**
     * В графе допускаются петли (рёбра, ведущие из вершины в саму себя).
     * <p>Тип: {@link Boolean}
     */
    ALLOW_LOOPS("allowLoops"),

    /**
     * Ограничения или связи между переменными.
     * <p>Тип: {@link String}, например "m ≤ n", "r - l = n"
     */
    RELATION("relation");

    private final String key;

    PropKey(String key) {
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
