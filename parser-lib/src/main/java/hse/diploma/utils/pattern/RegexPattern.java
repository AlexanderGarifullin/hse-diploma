package hse.diploma.utils.pattern;

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
}
