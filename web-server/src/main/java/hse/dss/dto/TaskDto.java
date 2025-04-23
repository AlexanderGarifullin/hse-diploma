package hse.dss.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.*;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDto {
    private Long id;

    @NotBlank(message = "Имя задачи не должно быть пустым")
    @Size(max = 255, message = "Имя слишком длинное")
    private String name;

    @NotNull(message = "Укажите ограничение по времени")
    @Min(value = 1, message = "Время должно быть положительным")
    private Integer timeLimit;

    @NotNull(message = "Укажите ограничение по памяти")
    @Min(value = 1, message = "Память должна быть положительной")
    private Integer memoryLimit;

    @NotBlank(message = "Легенда не должна быть пустой")
    private String legend;

    @NotBlank(message = "Входные данные не должны быть пустыми")
    private String inputData;

    @NotBlank(message = "Выходные данные не должны быть пустыми")
    private String outputData;
}