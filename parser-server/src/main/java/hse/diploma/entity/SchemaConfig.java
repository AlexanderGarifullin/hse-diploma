package hse.diploma.entity;

import hse.diploma.model.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "task_schemas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchemaConfig {

    @Id
    private String taskId;

    private Instant updatedAt;

    private Schema schema;
}
