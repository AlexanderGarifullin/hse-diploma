package hse.diploma.utility.structure;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class PairTest {

    @Test
    void equalsAndHashCode_work() {
        Pair<Integer,Integer> p1 = Pair.of(1,2);
        Pair<Integer,Integer> p2 = Pair.of(1,2);
        assertThat(p1).isEqualTo(p2).hasSameHashCodeAs(p2);
    }

    @Test
    void toString_format() {
        assertThat(Pair.of("a","b").toString()).isEqualTo("(a, b)");
    }
}
