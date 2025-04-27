package hse.diploma.utility.convertor;

import hse.diploma.model.TestCase;
import lombok.experimental.UtilityClass;

import java.util.LinkedList;
import java.util.List;

@UtilityClass
public class Convertor {

    public static List<String> fromListTestCaseToListTest(List<TestCase> testCases) {
        List<String> test = new LinkedList<>();
        for (TestCase testCase : testCases) {
            test.add(String.valueOf(testCase.getTest()));
        }
        return test;
    }
}
