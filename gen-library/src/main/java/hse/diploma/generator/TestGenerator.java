package hse.diploma.generator;

import hse.diploma.enums.props.PropKey;
import hse.diploma.model.Schema;
import hse.diploma.utility.structure.SeededRandomGenerator;
import lombok.experimental.UtilityClass;

import java.util.*;

import static hse.diploma.generator.TestMultiGenerator.generateMultiTests;
import static hse.diploma.generator.TestSingleGenerator.generateSingleTests;

@UtilityClass
public class TestGenerator {
    public static List<String> generate(Schema schema, Long seed) {
        if (seed == null) seed = System.currentTimeMillis();
        SeededRandomGenerator generator = new SeededRandomGenerator(seed);
        List<String> tests = new LinkedList<>();
        if (schema.vars().isEmpty()) return tests;
        if (schema.vars().getFirst().props().containsKey(PropKey.IS_TEST_CASE_VAR.key())) {
            tests = generateMultiTests(schema, generator);
        } else {
            tests = generateSingleTests(schema, generator);
        }
        return tests;
    }
}
