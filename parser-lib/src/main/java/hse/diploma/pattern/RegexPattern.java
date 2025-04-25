package hse.diploma.pattern;

import lombok.experimental.UtilityClass;

import java.util.regex.Pattern;

@UtilityClass
public class RegexPattern {
    public static final Pattern PAT_TEST_BLOCK = Pattern.compile(
            "(?ix)                          # включаем режим free-spacing и игнорируем регистр\n" +
                    "(?:                            # альтернатива начало описания мультитестов\n" +
                    "  (?:каждый[\\s\\S]{0,50}?тест)      # «каждый тест…»\n" +
                    "|(?:первая[\\s\\S]{0,50}?строка)     # или «первая строка…»\n" +
                    ")\\s*                          # немного текста до\n" +
                    "(?:содержит|находится)\\s+      # ключевые слова «содержит» или «находится»\n" +
                    "(?:одно|целое\\s+)?целое\\s+число # «одно целое число»\n" +
                    "\\s+(?<name>\\w+)              # имя переменной (t, T и т.п.)\n" +
                    "\\s*\\(                        # открывающая скобка перед диапазоном\n" +
                    "(?<range>[^)]+)                # захватываем диапазон (1≤t≤10^4)\n" +
                    "\\)\\s*                        # закрывающая скобка\n" +
                    "—\\s*количество               # «— количество …»\n",
            Pattern.UNICODE_CHARACTER_CLASS);

    public static final Pattern RANGE_BLOCK = Pattern.compile("(-?\\d+(?:e\\d+)?|\\d+\\*?10\\^\\d+)");

    public static final Pattern ONE_SCALAR = Pattern.compile(
            "(?:одно\\s+)?целое\\s+число\\s+(?<name>[a-zA-Z_][a-zA-Z_0-9]*)\\s*\\(\\s*(?<min>[^<]+)<=\\s*[^<]+<=\\s*(?<max>[^\\)]+)\\s*\\)",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
    );

    public static final Pattern ARR_SCALAR = Pattern.compile(
            // 1) len = n / m / …
            "(?<len>\\w+)" +
                    // 2) строго «целых чисел»
                    "\\s+целых\\s+чисел\\s+" +
                    // 3) имя a/b/c…
                    "(?<name>[A-Za-z])\\d?" +
                    // 4) любой текст (≠ '(') до '('
                    "[^(]*\\(" +
                    // 5) границы: min <= … <= max
                    "\\s*(?<min>[^<\\s]+)\\s*<=\\s*[^<\\s]+\\s*<=\\s*(?<max>[^)\\s]+)\\s*" +
                    "\\)",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
    );

}
