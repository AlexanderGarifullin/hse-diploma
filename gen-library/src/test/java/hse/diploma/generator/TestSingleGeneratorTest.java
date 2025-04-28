// src/test/java/hse/diploma/generator/TestSingleGeneratorTest.java
package hse.diploma.generator;

import hse.diploma.enums.Container;
import hse.diploma.enums.BaseType;
import hse.diploma.enums.props.PropKey;
import hse.diploma.model.Schema;
import hse.diploma.model.VarDescriptor;
import hse.diploma.utility.Settings;
import hse.diploma.utility.structure.SeededRandomGenerator;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class TestSingleGeneratorTest {

    @Test
    void generateSingleTests_producesUpToMaxUniqueTests_andCorrectFormat() {
        // Один скаляр (min=1, max=3)
        VarDescriptor desc = new VarDescriptor(
                "x",
                Container.SCALAR,
                BaseType.INTEGER,
                Map.of(PropKey.MIN.key(), 1L, PropKey.MAX.key(), 3L)
        );
        Schema schema = new Schema(List.of(desc));

        long seed = 42L;
        List<String> tests = TestSingleGenerator.generateSingleTests(
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
        // 3) каждая строчка оканчивается "\n"
        assertThat(tests).allMatch(s -> s.endsWith("\n"));
    }
}
