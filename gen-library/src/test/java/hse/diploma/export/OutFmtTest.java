package hse.diploma.export;

import hse.diploma.enums.Container;
import hse.diploma.enums.props.PropKey;
import hse.diploma.enums.props.values.LineType;
import hse.diploma.model.VarDescriptor;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

class OutFmtTest {

    @Test
    void format_scalar_singleLine() {
        VarDescriptor v = new VarDescriptor("x", Container.SCALAR, null,
                Map.of(PropKey.LINE_TYPE.key(), LineType.SINGLE.key()));
        String out = OutFmt.format(List.of(v), Map.of("x", 42));
        assertThat(out).isEqualTo("42");
    }

    @Test
    void format_array_singleLine() {
        VarDescriptor v = new VarDescriptor("a", Container.ARRAY, null,
                Map.of(PropKey.LINE_TYPE.key(), LineType.SINGLE.key()));
        List<Integer> arr = List.of(1,2,3);
        String out = OutFmt.format(List.of(v), Map.of("a", arr));
        assertThat(out).isEqualTo("1 2 3");
    }

    @Test
    void format_array_multiLine() {
        VarDescriptor v = new VarDescriptor("b", Container.ARRAY, null,
                Map.of(PropKey.LINE_TYPE.key(), LineType.MULTI.key()));
        List<String> list = List.of("foo","bar");
        String out = OutFmt.format(List.of(v), Map.of("b", list));
        assertThat(out).isEqualTo("foo\nbar");
    }

    @Test
    void format_stripTrailingNewline() {
        VarDescriptor v = new VarDescriptor("x", Container.SCALAR, null, Map.of());
        String out = OutFmt.format(List.of(v), Map.of("x", 9));
        assertThat(out).isEqualTo("9");
    }
}
