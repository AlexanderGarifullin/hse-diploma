package hse.diploma.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
public class TestCase {
    private Map<String, Object> context;
    private StringBuilder test;

    public void addToTest(Number x) {
        test.append(x.toString()).append("\n");
    }

    public void addToTest(List<? extends Number> x) {
        for (var num : x) test.append(num.toString()).append(" ");
        if (!x.isEmpty()) {
            test.setLength(test.length() - 1);
        }
        test.append("\n");
    }
}
