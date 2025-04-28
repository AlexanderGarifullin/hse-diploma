package hse.diploma.generator;

import hse.diploma.model.Schema;
import hse.diploma.model.VarDescriptor;
import hse.diploma.enums.props.PropKey;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestGeneratorTest {

    @Test
    void generate_emptySchema_returnsEmpty() {
        Schema schema = new Schema(new LinkedList<>());
        assertThat(TestGenerator.generate(schema, 1L)).isEmpty();
    }

    @Test
    void generate_dispatchesToMulti_whenTestCaseVarPresent() {
        var vd = new VarDescriptor("t", null, null,
                Map.of(PropKey.IS_TEST_CASE_VAR.key(), true,
                        PropKey.FIELDS.key(), List.of()));
        Schema schema = new Schema(new LinkedList<>(List.of(vd)));
        try (MockedStatic<TestMultiGenerator> multi = mockStatic(TestMultiGenerator.class)) {
            multi.when(() -> TestMultiGenerator.generateMultiTests(eq(schema), any()))
                    .thenReturn(List.of("m1"));
            List<String> out = TestGenerator.generate(schema, 1234L);
            assertThat(out).containsExactly("m1");
        }
    }

    @Test
    void generate_dispatchesToSingle_whenNoTestCaseVar() {
        var vd = new VarDescriptor("x", null, null, Map.of());
        Schema schema = new Schema(new LinkedList<>(List.of(vd)));
        try (MockedStatic<TestSingleGenerator> single = mockStatic(TestSingleGenerator.class)) {
            single.when(() -> TestSingleGenerator.generateSingleTests(eq(schema), any()))
                    .thenReturn(List.of("s1","s2"));
            List<String> out = TestGenerator.generate(schema, 4321L);
            assertThat(out).containsExactly("s1","s2");
        }
    }
}
