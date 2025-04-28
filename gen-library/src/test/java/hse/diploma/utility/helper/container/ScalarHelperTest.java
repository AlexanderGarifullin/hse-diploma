package hse.diploma.utility.helper.container;

import hse.diploma.enums.props.PropKey;
import hse.diploma.model.VarDescriptor;
import hse.diploma.utility.Settings;
import hse.diploma.utility.structure.Pair;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class ScalarHelperTest {

    @Test
    void getBoundaries_minMax() {
        VarDescriptor v = new VarDescriptor("x", null, null,
                Map.of(PropKey.MIN.key(), 1L, PropKey.MAX.key(), 4L));
        assertThat(ScalarHelper.getBoundaries(v, Map.of()))
                .isEqualTo(Pair.of(1L,4L));
    }

    @Test
    void getSmallBoundaries_respectsPercent() {
        var b = Pair.of(0L, 100L);
        var small = ScalarHelper.getSmallBoundaries(b);
        // PERCENT_SMALL = 20 → max ≤ min + length/20 = 0+100/20=5 or MIN_THRESHOLD_OFFSET (5)⇒5
        assertThat(small.second).isEqualTo(5L);
    }

    @Test
    void getBigBoundaries_respectsPercent() {
        var b = Pair.of(0L, 100L);
        var big = ScalarHelper.getBigBoundaries(b);
        // PERCENT_BIG=20 → min ≥ max - length/20 = 100-5=95 or max-5(offset)=95
        assertThat(big.first).isEqualTo(95L);
    }
}
