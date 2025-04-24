package hse.diploma;

import hse.diploma.export.Export;
import hse.diploma.parser.SchemaRegexBuilder;

import java.io.IOException;
import java.nio.file.Path;

public class ParserMain {
    public static void main(String[] args) throws IOException {
        String latex = """
            Первая строка содержит целое число t (1≤t≤10^4) — количество тестов.
            Первая строка каждого теста содержит целое число n (1≤n≤2⋅10^5).
            Вторая строка каждого теста содержит n целых чисел a1,a2,…,a_n (1≤a_i≤n).
            Гарантируется, что сумма n по всем тестам не превосходит 2⋅10^5.
            """;
        var schema = SchemaRegexBuilder.parse(latex);
        System.out.println(Export.asPrettyText(schema));
        Export.saveToJson(schema, Path.of("parser-lib-samples/schema.json"));
    }
}