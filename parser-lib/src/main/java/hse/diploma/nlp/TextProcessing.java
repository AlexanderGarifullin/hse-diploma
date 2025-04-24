package hse.diploma.nlp;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TextProcessing {

    public static String normalize(String text) {
        if (text == null) return "";

        // 1) убрать LaTeX-математику
        text = normalizeMath(text);

        // 2) снять простые текстовые команды
        text = normalizeCommands(text);

        // 3) специальные символы и команды
        text = normalizeSpecial(text);

        // 4) убрать LaTeX-комментарии
        text = normalizeComments(text);

        // 5) привести пробелы/переносы строк
        text = normalizeSpaces(text);

        return text;
    }

    private static String normalizeMath(String text) {
        return text.replaceAll("\\$(.*?)\\$", "$1")
                .replaceAll("\\\\\\((.*?)\\\\\\)", "$1")
                .replaceAll("\\\\\\[(.*?)\\\\\\]", "$1")
                .replaceAll("\\^(\\{(\\d+)})?", "^$2");
    }

    private static String normalizeCommands(String text) {
        return text.replaceAll("\\\\textit\\{([^}]*)}", "$1")
                .replaceAll("\\\\emph\\{([^}]*)}", "$1")
                .replaceAll("\\\\underline\\{([^}]*)}", "$1");
    }

    private static String normalizeSpecial(String text) {
        return text.replaceAll("(?i)\\\\cdot", "*")
                .replaceAll("(?i)\\\\times", "*")
                .replaceAll("⋅", "*")
                .replaceAll("≤", "<=").replaceAll("≥", ">=")
                .replaceAll("(?i)\\\\leq?", "<=")
                .replaceAll("(?i)\\\\geq?", ">=")
                .replaceAll("(?i)\\\\ldots", "...")
                .replaceAll("…", "...");
    }

    private static String normalizeComments(String text) {
        return text.replaceAll("%.*?\\n", " ");
    }

    private static String normalizeSpaces(String text) {
        return text.replaceAll("[\\r\\f]+", "\n")
                .replaceAll("[ \\t]+", " ")
                .replaceAll(" *\\n+", "\n")
                .replaceAll("\\n+", "\n")
                .trim();
    }
}
