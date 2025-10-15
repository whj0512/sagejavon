package com.springboot.cli.model.VO.python.exercise;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExerciseVOPythonPage {
    private List<ExercisePythonVO> exerciseList;
    private Long total;
    private Long pages;
}
