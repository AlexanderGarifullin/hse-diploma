package hse.diploma.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import hse.diploma.model.Schema;
import hse.diploma.model.VarDescriptor;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Утилитный класс для экспорта схемы в текстовый и JSON-форматы.
 */
@UtilityClass
public class Export {
    /**
     * Преобразует схему в читаемый текст: имя, тип, props.
     */
    public String asPrettyText(Schema schema) {
        StringBuilder sb = new StringBuilder();
        for (VarDescriptor v : schema.vars()) {
            sb.append(v.name())
                    .append(" : ").append(v.container())
                    .append("/").append(v.baseType())
                    .append(" → ").append(v.props())
                    .append("\n");
        }
        return sb.toString();
    }

    /**
     * Сохраняет схему в JSON-файл по заданному пути.
     */
    public static void saveToJson(Schema schema, Path path) throws IOException {
        ObjectMapper om = new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT);
        om.writeValue(path.toFile(), schema.vars());
    }
}
