// src/test/java/hse/diploma/generator/TestMultiGeneratorTest.java
package hse.diploma.generator;

import hse.diploma.enums.Container;
import hse.diploma.enums.BaseType;
import hse.diploma.enums.props.PropKey;
import hse.diploma.model.Schema;
import hse.diploma.model.VarDescriptor;
import hse.diploma.utility.Settings;
import hse.diploma.utility.structure.SeededRandomGenerator;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class TestMultiGeneratorTest {

    @Test
    void generateMultiTests_producesUpToMaxUniqueTests_andCorrectFormat() {
        VarDescriptor field = new VarDescriptor(
                "n",
                Container.SCALAR,
                BaseType.INTEGER,
                Map.of(PropKey.MIN.key(), 1L, PropKey.MAX.key(), 2L)
        );

        VarDescriptor top = new VarDescriptor(
                "t",
                Container.SCALAR,
                BaseType.INTEGER,
                Map.of(PropKey.FIELDS.key(), List.of(field))
        );
        LinkedList<VarDescriptor> vars = new LinkedList<>();
        vars.add(top);
        Schema schema = new Schema(vars);

        long seed = 42L;
        List<String> tests = TestMultiGenerator.generateMultiTests(
                schema,
                new SeededRandomGenerator(seed)
        );

        int maxCount = Settings.CNT_SMALL_TESTS
                + Settings.CNT_RANDOM_TESTS
                + Settings.CNT_BIG_TESTS;
        // 1) не больше максимума
        assertThat(tests).hasSizeLessThanOrEqualTo(maxCount);
        // 2) без дубликатов
        assertThat(tests).doesNotHaveDuplicates();
        // 3) каждая строка начинается с числа и "\n"
        assertThat(tests).allMatch(s -> s.matches("^\\d+\\n[\\s\\S]*$"));
    }
}
