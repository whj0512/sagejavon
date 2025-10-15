package com.springboot.cli.service.python;

import com.springboot.cli.model.DO.python.ExerciseRecordPythonDO;
import com.springboot.cli.model.VO.exercise.ExerciseRecordPage;
import com.springboot.cli.model.VO.python.exercise.ExerciseRecordPythonPage;

import java.util.List;

public interface ExerciseRecordPythonService {
    Integer hasDoneExercise(String studentId, Long exerciseId);

    List<ExerciseRecordPythonDO> getExerciseRecord(String studentId, Long questionId);

    ExerciseRecordPythonDO getExerciseRecord(Long recordId);

    List<ExerciseRecordPythonDO> getStudentAllExerciseRecord(String studentId);

    int getExerciseRecordsByExerciseID(Long exerciseID);

    ExerciseRecordPythonPage page(Integer pageSize, Integer pageNum, String studentId);
}
