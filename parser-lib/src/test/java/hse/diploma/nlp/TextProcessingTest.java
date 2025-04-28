package hse.diploma.nlp;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class TextProcessingTest {

    @Test
    void normalize_nullReturnsEmpty() {
        assertThat(TextProcessing.normalize(null)).isEmpty();
    }

    @Test
    void normalize_removesLatexAndCommandsAndExtraSpaces() {
        String raw = "%comment\n" +
                " \\textit{Hello} $x+1$  \n" +
                "\\cdot test …  \\underline{U}\\newline";
        String norm = TextProcessing.normalize(raw);
        // проверяем что нет $,\textit,\cdot,…
        assertThat(norm)
                .doesNotContain("$")
                .doesNotContain("\\textit")
                .contains("Hello x+1")
                .contains("* test")
                .contains("U");
    }
}
