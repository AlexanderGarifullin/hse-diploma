package hse.dss.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

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

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    private List<Test> tests;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    private List<Solution> solutions;

    @OneToOne(mappedBy = "task", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Checker checker;

    @OneToOne(mappedBy = "task", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Validator validators;
}
