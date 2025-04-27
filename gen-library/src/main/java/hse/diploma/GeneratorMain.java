package hse.diploma;

import hse.diploma.export.Export;
import hse.diploma.generator.TestGenerator;
import hse.diploma.loader.SchemaLoader;
import hse.diploma.model.Schema;


import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class GeneratorMain {
    public static void main(String[] args) throws IOException {
        String schemaPath = "D:/d/parser-lib-samples/schema.json";
        long seed = 42L;

        SchemaLoader loader = new SchemaLoader();
        Schema schema = loader.load(new File(schemaPath));


        Export.saveToJson(schema, Path.of("parser-lib-samples/schema2.json"));


        List<String> tests = TestGenerator.generate(schema, seed);


        Path outputDir = Path.of("gen-lib-samples");
        if (Files.exists(outputDir)) {
            try (var files = Files.list(outputDir)) {
                files.forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException e) {
                        throw new RuntimeException("Cannot delete file: " + path, e);
                    }
                });
            }
        } else {
            Files.createDirectories(outputDir);
        }

        for (int i = 0; i < tests.size(); i++) {
            String test = tests.get(i);
            String fileName = "test" + (i + 1) + ".txt";
            Path filePath = outputDir.resolve(fileName);
            Files.writeString(filePath, test);
        }

        System.out.println("Completed. Save " + tests.size() + " tests in " + outputDir.toAbsolutePath());
    }
}
