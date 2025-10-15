package com.springboot.cli.model.VO.python.exercise;

import com.springboot.cli.model.DO.ExerciseDO;
import com.springboot.cli.model.DO.python.ExercisePythonDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExercisePythonPage {
    private List<ExercisePythonDO> exerciseList;
    private Long total;
    private Long pages;
}
