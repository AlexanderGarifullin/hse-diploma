package hse.diploma.parser.array;

import hse.diploma.enums.props.values.SortedOrder;
import lombok.experimental.UtilityClass;

/**
 * Утилитный класс для определения порядка элементов в массиве.
 * Используется для флага sortedOrder (возрастающий, неубывающий, убывающий и т.п.).
 */
@UtilityClass
public class ArrayParser {
    /**
     * Простейшая эвристика для определения порядка:
     * - "a1 < a2" → ASC
     * - "неубыв", "возрастающ" → ASC
     * - "убыв", "невозрастающ" → DESC
     * - иначе — NONE
     */
    public static SortedOrder detectOrder(String chunk) {
        String s = chunk.toLowerCase();

        // 1. a1 < a2
        if (s.matches(".*?\\b[a-z]\\d?\\s*<\\s*[a-z]\\d?.*")) {
            return SortedOrder.ASC;
        }
        // 2. «неубыв…» → ASC
        if (s.contains("неубыв")) {
            return SortedOrder.ASC;
        }
        // 3. «невозраст…» → DESC
        if (s.contains("невозраст")) {
            return SortedOrder.DESC;
        }
        // 4. «возраст…» → ASC
        if (s.contains("возраст")) {
            return SortedOrder.ASC;
        }
        // 5. «убыв…» → DESC
        if (s.contains("убыв")) {
            return SortedOrder.DESC;
        }
        return SortedOrder.NONE;
    }


}
