package hse.diploma.generator;

import hse.diploma.enums.Container;
import hse.diploma.model.Schema;
import hse.diploma.model.TestCase;
import hse.diploma.model.VarDescriptor;
import hse.diploma.strategy.ArrayGenerator;
import hse.diploma.strategy.ScalarGenerator;
import hse.diploma.utility.structure.SeededRandomGenerator;
import hse.diploma.utility.Settings;
import lombok.experimental.UtilityClass;

import java.util.*;

import static hse.diploma.utility.convertor.Convertor.fromListTestCaseToListTest;

@UtilityClass
public class TestSingleGenerator {

    static List<String> generateSingleTests(Schema schema, SeededRandomGenerator generator) {
        // хэш всех тестов
        Set<String> hash = new HashSet<>();
        List<TestCase> testCases = new LinkedList<>();
        testCases.addAll(generateSingleSmallTest(hash, schema.vars(), generator));
        testCases.addAll(generateSingleRandomTest(hash, schema.vars(), generator));
        testCases.addAll(generateSingleBigTest(hash, schema.vars(), generator));
        return fromListTestCaseToListTest(testCases);
    }

    private static List<TestCase> generateSingleSmallTest(Set<String> hash, List<VarDescriptor> vars, SeededRandomGenerator generator) {
        List<TestCase> testCases = new LinkedList<>();
        for (int i = 0; i < Settings.CNT_SMALL_TESTS; i++) {
            var testCase = generateOneSingleSmallTest(vars, generator);
            if (!hash.contains(testCase.getTest().toString())) {
                hash.add(testCase.getTest().toString());
                testCases.add(testCase);
            }
        }
        return testCases;
    }

    private static List<TestCase> generateSingleBigTest(Set<String> hash , List<VarDescriptor> vars, SeededRandomGenerator generator) {
        List<TestCase> testCases = new LinkedList<>();
        for (int i = 0; i < Settings.CNT_BIG_TESTS; i++) {
            var testCase = generateOneSingleBigTest(vars, generator);
            if (!hash.contains(testCase.getTest().toString())) {
                hash.add(testCase.getTest().toString());
                testCases.add(testCase);
            }
        }
        return testCases;
    }

    private static List<TestCase> generateSingleRandomTest(Set<String> hash , List<VarDescriptor> vars, SeededRandomGenerator generator) {
        List<TestCase> testCases = new LinkedList<>();
        for (int i = 0; i < Settings.CNT_RANDOM_TESTS; i++) {
            var testCase = generateOneSingleRandomTest(vars, generator);
            if (!hash.contains(testCase.getTest().toString())) {
                hash.add(testCase.getTest().toString());
                testCases.add(testCase);
            }
        }
        return testCases;
    }

    static TestCase generateOneSingleSmallTest(List<VarDescriptor> vars, SeededRandomGenerator generator) {
        Map<String, Object> context = new HashMap<>();
        TestCase testCase = new TestCase(context, new StringBuilder());
        for (VarDescriptor descriptor : vars) {
            if (descriptor.container().equals(Container.SCALAR)) {
                ScalarGenerator.generateSmallScalar(descriptor, testCase, generator);
            }
            else if (descriptor.container().equals(Container.ARRAY)) {
                ArrayGenerator.generateSmallArray(descriptor, testCase, generator);
            }
        }
        return testCase;
    }

    static TestCase generateOneSingleBigTest(List<VarDescriptor> vars, SeededRandomGenerator generator) {
        Map<String, Object> context = new HashMap<>();
        TestCase testCase = new TestCase(context, new StringBuilder());
        for (VarDescriptor descriptor : vars) {
            if (descriptor.container().equals(Container.SCALAR)) {
                ScalarGenerator.generateBigScalar(descriptor, testCase, generator);
            }
            else if (descriptor.container().equals(Container.ARRAY)) {
                ArrayGenerator.generateBigArray(descriptor, testCase, generator);
            }
        }
        return testCase;
    }


    static TestCase generateOneSingleRandomTest(List<VarDescriptor> vars, SeededRandomGenerator generator) {
        Map<String, Object> context = new HashMap<>();
        TestCase testCase = new TestCase(context, new StringBuilder());
        for (VarDescriptor descriptor : vars) {
            if (descriptor.container().equals(Container.SCALAR)) {
                ScalarGenerator.generateRandomScalar(descriptor, testCase, generator);
            }
            else if (descriptor.container().equals(Container.ARRAY)) {
                ArrayGenerator.generateRandomArray(descriptor, testCase, generator);
            }
        }
        return testCase;
    }
}
