package hse.diploma.strategy;

import hse.diploma.model.TestCase;
import hse.diploma.model.VarDescriptor;
import hse.diploma.utility.helper.container.ArrayHelper;
import hse.diploma.utility.structure.Pair;
import hse.diploma.utility.helper.container.ScalarHelper;
import hse.diploma.utility.structure.SeededRandomGenerator;

import java.util.LinkedList;
import java.util.List;

public class ArrayGenerator {

    public static void generateSmallArray(VarDescriptor var, TestCase testCase, SeededRandomGenerator generator) {
        Pair<Long, Long> arrayValuesBoundaries = ScalarHelper.getBoundaries(var, testCase.getContext());
        Pair<Long, Long> arrayLensBoundaries = ArrayHelper.getLenBoundaries(var, testCase.getContext());

        arrayValuesBoundaries = ScalarHelper.getSmallBoundaries(arrayValuesBoundaries);
        arrayLensBoundaries = ScalarHelper.getSmallBoundaries(arrayLensBoundaries);

        List<Long> array = getRandomArray(arrayValuesBoundaries, arrayLensBoundaries, generator);
        addArrayToTestCase(var.name(), array, testCase);
    }

    public static void generateRandomArray(VarDescriptor var, TestCase testCase, SeededRandomGenerator generator) {
        Pair<Long, Long> arrayValuesBoundaries = ScalarHelper.getBoundaries(var, testCase.getContext());
        Pair<Long, Long> arrayLensBoundaries = ArrayHelper.getLenBoundaries(var, testCase.getContext());
        List<Long> array = getRandomArray(arrayValuesBoundaries, arrayLensBoundaries, generator);
        addArrayToTestCase(var.name(), array, testCase);
    }

    public static void generateBigArray(VarDescriptor var, TestCase testCase, SeededRandomGenerator generator) {
        Pair<Long, Long> arrayValuesBoundaries = ScalarHelper.getBoundaries(var, testCase.getContext());
        Pair<Long, Long> arrayLensBoundaries = ArrayHelper.getLenBoundaries(var, testCase.getContext());

        arrayValuesBoundaries = ScalarHelper.getBigBoundaries(arrayValuesBoundaries);
        arrayLensBoundaries = ScalarHelper.getBigBoundaries(arrayLensBoundaries);

        List<Long> array = getRandomArray(arrayValuesBoundaries, arrayLensBoundaries, generator);
        addArrayToTestCase(var.name(), array, testCase);
    }

    private static void addArrayToTestCase(String name, List<Long> array, TestCase testCase) {
        testCase.getContext().put(name, array);
        testCase.addToTest(array);
    }

    private static List<Long> getRandomArray(Pair<Long, Long> arrayValuesBoundaries,
                                             Pair<Long, Long> arrayLensBoundaries,
                                             SeededRandomGenerator generator) {
        List<Long> array = new LinkedList<>();

        long len = generator.nextIntInclusive(arrayLensBoundaries);
        for (int i = 0; i < len; i++) {
            long value = generator.nextIntInclusive(arrayValuesBoundaries);
            array.add(value);
        }

        return array;
    }
}
