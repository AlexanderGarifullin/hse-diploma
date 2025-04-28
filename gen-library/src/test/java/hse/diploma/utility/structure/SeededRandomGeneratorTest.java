package hse.diploma.utility.structure;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class SeededRandomGeneratorTest {

    @Test
    void nextIntInclusive_reproducible() {
        long seed = 12345L;
        SeededRandomGenerator g1 = new SeededRandomGenerator(seed);
        SeededRandomGenerator g2 = new SeededRandomGenerator(seed);
        for (int i=0; i<10; i++) {
            long x1 = g1.nextIntInclusive(0, 100);
            long x2 = g2.nextIntInclusive(0, 100);
            assertThat(x1).isEqualTo(x2);
        }
    }

    @Test
    void nextIntInclusive_invalidBounds_throws() {
        SeededRandomGenerator g = new SeededRandomGenerator(1L);
        assertThatThrownBy(() -> g.nextIntInclusive(5, 4))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nextIntInclusive_pairOverload() {
        Pair<Long,Long> bounds = Pair.of(10L, 12L);
        long val = new SeededRandomGenerator(99L).nextIntInclusive(bounds);
        assertThat(val).isBetween(10L,12L);
    }
}
