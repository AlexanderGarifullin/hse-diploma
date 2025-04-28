package hse.diploma.parser.math;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class MathParserTest {

    @Test
    void parseNumber_plainInteger() {
        assertThat(MathParser.parseNumber("123")).isEqualTo(123L);
        assertThat(MathParser.parseNumber("-42")).isEqualTo(-42L);
    }

    @Test
    void parseNumber_scientificNotation() {
        assertThat(MathParser.parseNumber("2e5")).isEqualTo(200_000L);
        assertThat(MathParser.parseNumber("3e+3")).isEqualTo(3_000L);
    }

    @Test
    void parseNumber_powerNotation() {
        assertThat(MathParser.parseNumber("10^4")).isEqualTo(10_000L);
        assertThat(MathParser.parseNumber("2*10^3")).isEqualTo(2_000L);
    }

    @Test
    void parseNumber_invalid_throws() {
        assertThatThrownBy(() -> MathParser.parseNumber("foo"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Невозможно распознать число");
    }

    @Test
    void parseRange_twoNumbers() {
        long[] rng = MathParser.parseRange("1<=x<=100");
        assertThat(rng).containsExactly(1L, 100L);
    }

    @Test
    void parseRange_singleNumber() {
        long[] rng = MathParser.parseRange("0<=k<=0");
        assertThat(rng).containsExactly(0L, 0L);
    }

    @Test
    void parseRange_noNumbers_returnsMinMax() {
        long[] rng = MathParser.parseRange("no digits here");
        assertThat(rng[0]).isLessThan(rng[1]);
    }
}
