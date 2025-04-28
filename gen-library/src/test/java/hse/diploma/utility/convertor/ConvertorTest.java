package hse.diploma.utility.convertor;

import hse.diploma.model.TestCase;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

class ConvertorTest {

    @Test
    void fromListTestCaseToListTest_preservesOrderAndToString() {
        TestCase t1 = new TestCase(Map.of(), new StringBuilder("one\n"));
        TestCase t2 = new TestCase(Map.of(), new StringBuilder("two\n"));
        List<String> result = Convertor.fromListTestCaseToListTest(List.of(t1,t2));
        assertThat(result).containsExactly("one\n", "two\n");
    }

    @Test
    void fromListTestCaseToListTest_emptyList() {
        assertThat(Convertor.fromListTestCaseToListTest(List.of())).isEmpty();
    }
}
