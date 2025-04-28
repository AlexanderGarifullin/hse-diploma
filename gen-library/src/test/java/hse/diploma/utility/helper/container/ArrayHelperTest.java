package hse.diploma.utility.helper.container;

import hse.diploma.enums.props.PropKey;
import hse.diploma.model.VarDescriptor;
import hse.diploma.utility.structure.Pair;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

class ArrayHelperTest {

    @Test
    void getLenBoundaries_fixed() {
        VarDescriptor v = new VarDescriptor("n", null, null,
                Map.of(PropKey.MIN_LEN.key(), 2L, PropKey.MAX_LEN.key(), 5L));
        assertThat(ArrayHelper.getLenBoundaries(v, Map.of()))
                .isEqualTo(Pair.of(2L,5L));
    }

    @Test
    void getLenBoundaries_fromContext() {
        VarDescriptor v = new VarDescriptor("m", null, null,
                Map.of(PropKey.VAR_MIN_LEN.key(),"n", PropKey.VAR_MAX_LEN.key(),"n"));
        Map<String,Object> ctx = Map.of("n", 7L);
        assertThat(ArrayHelper.getLenBoundaries(v, ctx))
                .isEqualTo(Pair.of(7L,7L));
    }
}
