package hse.diploma.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "tasks", schema = "diplom")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String legend;

    @Column(name = "input", nullable = false)
    private String inputData;

    @Column(name = "output", nullable = false)
    private String outputData;

    @Column(name = "time_limit", nullable = false)
    private Integer timeLimit;

    @Column(name = "memery_limit", nullable = false)
    private Integer memoryLimit;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
