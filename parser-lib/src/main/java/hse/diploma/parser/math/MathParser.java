package hse.diploma.parser.math;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static hse.diploma.pattern.RegexPattern.RANGE_BLOCK;
/**
 * Утилитный класс для работы с математическими выражениями, встречающимися в диапазонах (например, 2*10^5, 10^4, 1e5).
 */
@UtilityClass
public class MathParser {
    /**
     * Извлекает два числа из диапазона вида "1<=x<=n" или "0<=k<=10^5".
     * Если найдены одно или два числа, возвращает массив из двух элементов.
     */
    public static long[] parseRange(String rangeText) {
        Matcher m = RANGE_BLOCK.matcher(rangeText);
        List<Long> nums = new ArrayList<>();
        while (m.find()) {
            nums.add(parseNumber(m.group()));
        }

        if (nums.size() >= 2) {
            return new long[]{ nums.get(0), nums.get(1) };
        } else if (nums.size() == 1) {
            long v = nums.get(0);
            return new long[]{ v, v };
        } else {
            return new long[]{ Long.MIN_VALUE, Long.MAX_VALUE };
        }
    }

    /**
     * Преобразует строковое представление числа в long.
     * Поддерживает форматы:
     * - обычные числа: 123
     * - научная нотация: 2e5
     * - степени десятки: 2*10^5, 10^6 и т.п.
     */
    public static long parseNumber(String s) {
        s = s.replaceAll("\\s+", ""); // удалить пробелы

        try {
            // Обычное число, например: 123
            if (s.matches("-?\\d+")) {
                return Long.parseLong(s);
            }

            // Научная нотация: 2e5 или 2e+5
            if (s.matches("-?\\d+e\\+?\\d+")) {
                String[] parts = s.split("e\\+?");
                long base = Long.parseLong(parts[0]);
                int exp = Integer.parseInt(parts[1]);
                return base * (long)Math.pow(10, exp);
            }

            // Формат: 2*10^5
            if (s.matches("-?\\d+\\*10\\^\\d+")) {
                Matcher m = Pattern.compile("(-?\\d+)\\*10\\^(\\d+)").matcher(s);
                if (m.matches()) {
                    long base = Long.parseLong(m.group(1));
                    int exp = Integer.parseInt(m.group(2));
                    return base * (long)Math.pow(10, exp);
                }
            }

            // Формат: 10^5
            if (s.matches("10\\^\\d+")) {
                int exp = Integer.parseInt(s.substring(s.indexOf('^') + 1));
                return (long)Math.pow(10, exp);
            }

        } catch (Exception e) {
            throw new IllegalArgumentException("Ошибка при парсинге числа: " + s, e);
        }

        throw new IllegalArgumentException("Невозможно распознать число: " + s);
    }
}
