package hse.dss.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "checkers",
        schema = "diplom",
        uniqueConstraints = @UniqueConstraint(columnNames = {"task_id", "code"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Checker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String code;

    @OneToOne
    @JoinColumn(name = "task_id", unique = true, nullable = false)
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_id", nullable = false)
    private ProgrammingLanguage language;
}
