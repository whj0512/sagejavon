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
public class ExerciseRecordVOPythonPage {
    private List<ExerciseRecordPythonVO> exerciseRecordList;
    private Long total;
    private Long pages;
}
