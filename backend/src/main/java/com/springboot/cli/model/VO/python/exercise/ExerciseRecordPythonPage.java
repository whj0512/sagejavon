package com.springboot.cli.model.VO.python.exercise;

import com.springboot.cli.model.DO.ExerciseRecordDO;
import com.springboot.cli.model.DO.python.ExerciseRecordPythonDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExerciseRecordPythonPage {
    private List<ExerciseRecordPythonDO> exerciseRecordList;
    private Long total;
    private Long pages;
}
