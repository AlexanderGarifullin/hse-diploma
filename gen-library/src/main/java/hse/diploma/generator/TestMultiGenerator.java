package hse.diploma.generator;

import hse.diploma.enums.props.PropKey;
import hse.diploma.model.Schema;
import hse.diploma.model.TestCase;
import hse.diploma.model.VarDescriptor;
import hse.diploma.utility.Settings;
import hse.diploma.utility.structure.Pair;
import hse.diploma.utility.structure.SeededRandomGenerator;
import lombok.experimental.UtilityClass;

import java.util.*;

import static hse.diploma.generator.TestSingleGenerator.*;
import static hse.diploma.utility.convertor.Convertor.fromListTestCaseToListTest;
import static hse.diploma.utility.helper.container.ScalarHelper.*;

@UtilityClass
public class TestMultiGenerator {
    static List<String> generateMultiTests(Schema schema, SeededRandomGenerator generator) {
        Set<String> multiHash = new HashSet<>();
        List<String> testCases = new LinkedList<>();
        List<VarDescriptor> vars = (List<VarDescriptor>) schema
                .vars()
                .getFirst()
                .props()
                .getOrDefault(PropKey.FIELDS.key(), List.of());
        VarDescriptor testDescriptor = schema.vars().getFirst();
        testCases.addAll(generateMultiSmallTest(multiHash, testDescriptor, vars, generator));
        testCases.addAll(generateMultiRandomTest(multiHash, testDescriptor, vars, generator));
        testCases.addAll(generateMultiBigTest(multiHash, testDescriptor, vars, generator));
        return testCases;
    }

    private static List<String> generateMultiBigTest(Set<String> hash, VarDescriptor testDescriptor,
                                                       List<VarDescriptor> vars, SeededRandomGenerator generator) {
        List<String> testCases = new LinkedList<>();
        for (int i = 0; i < Settings.CNT_BIG_TESTS; i++) {
            StringBuilder test = new StringBuilder();
            long cntTests = generateBigCountMultitests(testDescriptor, generator);
            test.append(cntTests).append("\n");
            for (int j = 0; j < cntTests; j++) {
                var testCase = generateOneSingleBigTest(vars, generator);
                test.append(testCase.getTest());
            }
            if (!hash.contains(test.toString())) {
                hash.add(test.toString());
                testCases.add(test.toString());
            }
        }
        return testCases;
    }

    private static List<String>generateMultiRandomTest(Set<String> hash, VarDescriptor testDescriptor,
                                                         List<VarDescriptor> vars, SeededRandomGenerator generator) {
        List<String> testCases = new LinkedList<>();
        for (int i = 0; i < Settings.CNT_RANDOM_TESTS; i++) {
            StringBuilder test = new StringBuilder();
            long cntTests = generateRandomCountMultitests(testDescriptor, generator);
            test.append(cntTests).append("\n");
            for (int j = 0; j < cntTests; j++) {
                var testCase = generateOneSingleRandomTest(vars, generator);
                test.append(testCase.getTest());
            }
            if (!hash.contains(test.toString())) {
                hash.add(test.toString());
                testCases.add(test.toString());
            };
        }
        return testCases;
    }

    private static List<String> generateMultiSmallTest(Set<String> hash, VarDescriptor testDescriptor,
                                                         List<VarDescriptor> vars, SeededRandomGenerator generator) {
        List<String> testCases = new LinkedList<>();
        for (int i = 0; i < Settings.CNT_SMALL_TESTS; i++) {
            StringBuilder test = new StringBuilder();
            long cntTests = generateSmallCountMultitests(testDescriptor, generator);
            test.append(cntTests).append("\n");
            for (int j = 0; j < cntTests; j++) {
                var testCase = generateOneSingleSmallTest(vars, generator);
                test.append(testCase.getTest());
            }
            if (!hash.contains(test.toString())) {
                hash.add(test.toString());
                testCases.add(test.toString());
            };
        }
        return testCases;
    }

    private static long generateBigCountMultitests(VarDescriptor test, SeededRandomGenerator generator) {
        Pair<Long, Long> boundaries = getBoundaries(test);
        boundaries = getBigBoundaries(boundaries);
        return generator.nextIntInclusive(boundaries);
    }

    private static long generateRandomCountMultitests(VarDescriptor test, SeededRandomGenerator generator) {
        Pair<Long, Long> boundaries = getBoundaries(test);
        return generator.nextIntInclusive(boundaries);
    }

    private static long generateSmallCountMultitests(VarDescriptor test, SeededRandomGenerator generator) {
        Pair<Long, Long> boundaries = getBoundaries(test);
        boundaries = getSmallBoundaries(boundaries);
        return generator.nextIntInclusive(boundaries);
    }
}
