package com.springboot.cli.model.VO.python;

import com.springboot.cli.model.VO.python.exercise.ExercisePythonVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AllInformationPythonVO {
    private Integer selectNumber;
    private Integer codeNumber;
    private LocalDateTime latestTime;
    private List<ExercisePythonVO> popularQuestion;
    private Integer solveDays;
    private Integer solveQuestions;
    private Map<LocalDate, Integer> solveNumbersPerDay;
}
