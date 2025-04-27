package hse.diploma.strategy;

import hse.diploma.model.TestCase;
import hse.diploma.model.VarDescriptor;
import hse.diploma.utility.structure.Pair;
import hse.diploma.utility.helper.container.ScalarHelper;
import hse.diploma.utility.structure.SeededRandomGenerator;


public class ScalarGenerator {
    public static void generateSmallScalar(VarDescriptor var, TestCase testCase, SeededRandomGenerator generator) {
        Pair<Long, Long> boundaries = ScalarHelper.getBoundaries(var, testCase.getContext());
        boundaries = ScalarHelper.getSmallBoundaries(boundaries);
        long value = generator.nextIntInclusive(boundaries.first, boundaries.second);
        addScalarToTestCase(var.name(), value, testCase);
    }

    public static void generateRandomScalar(VarDescriptor var, TestCase testCase, SeededRandomGenerator generator) {
        Pair<Long, Long> boundaries = ScalarHelper.getBoundaries(var, testCase.getContext());
        long value = generator.nextIntInclusive(boundaries.first, boundaries.second);
        addScalarToTestCase(var.name(), value, testCase);
    }

    public static void generateBigScalar(VarDescriptor var, TestCase testCase, SeededRandomGenerator generator) {
        Pair<Long, Long> boundaries = ScalarHelper.getBoundaries(var, testCase.getContext());
        boundaries = ScalarHelper.getBigBoundaries(boundaries);
        long value = generator.nextIntInclusive(boundaries.first, boundaries.second);
        addScalarToTestCase(var.name(), value, testCase);
    }

    private static void addScalarToTestCase(String name, Long value, TestCase testCase) {
        testCase.getContext().put(name, value);
        testCase.addToTest(value);
    }
}
