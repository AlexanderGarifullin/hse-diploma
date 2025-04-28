package hse.diploma.pattern;

import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;

import static org.assertj.core.api.Assertions.*;

class RegexPatternTest {

    @Test
    void oneScalar_matchesAndGroups() {
        String text = "целое число n ( 1 <= n <= 200 )";
        Matcher m = RegexPattern.ONE_SCALAR.matcher(text);
        assertThat(m.find()).isTrue();
        assertThat(m.group("name")).isEqualTo("n");
        assertThat(m.group("min").trim()).isEqualTo("1");
        assertThat(m.group("max").trim()).isEqualTo("200");
    }

    @Test
    void arrScalar_matchesAndGroups() {
        String text = "n целых чисел a1 ( 1 <= a_i <= n )";
        Matcher m = RegexPattern.ARR_SCALAR.matcher(text);
        assertThat(m.find()).isTrue();
        assertThat(m.group("len")).isEqualTo("n");
        assertThat(m.group("name")).isEqualTo("a");
        assertThat(m.group("min")).isEqualTo("1");
        assertThat(m.group("max")).isEqualTo("n");
    }

    @Test
    void testBlock_matchesAndGroups() {
        String text = "Первая строка содержит целое число t (1<=t<=10) — количество тестов";
        Matcher m = RegexPattern.PAT_TEST_BLOCK.matcher(text);
        assertThat(m.find()).isTrue();
        assertThat(m.group("name")).isEqualTo("t");
        assertThat(m.group("range")).isEqualTo("1<=t<=10");
    }
}
