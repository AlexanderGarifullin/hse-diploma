package hse.diploma.parser.array;

import hse.diploma.enums.props.values.SortedOrder;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ArrayParser {
    public static SortedOrder detectOrder(String chunk) {
        if (chunk.matches(".*?\\b[a-z]\\d?\\s*<\\s*[a-z]\\d?.*")) return SortedOrder.ASC;   // a1 < a2
        if (chunk.contains("неубыв") || chunk.contains("возраст")) return SortedOrder.ASC;  // неубывающ / возрастающ
        if (chunk.contains("невозраст") || chunk.contains("убыв")) return SortedOrder.DESC; // невозрастающ / убывающ
        return SortedOrder.NONE;
    }
}
