package com.springboot.cli.model.VO.python.exercise;

import com.springboot.cli.model.DO.ExerciseDO;
import com.springboot.cli.model.DO.ExerciseRecordDO;
import com.springboot.cli.model.DO.python.ExercisePythonDO;
import com.springboot.cli.model.DO.python.ExerciseRecordPythonDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SelectExerciseRecordPythonVO {
    private Long recordId;
    private Long choiceExerciseId;
    private String questionText;
    private List<KnowledgePythonVO> knowledgeConcept;
    private Integer score;
    private LocalDateTime submitTime;

    public SelectExerciseRecordPythonVO(ExercisePythonDO exercise, List<KnowledgePythonVO> knowledgeConcept, ExerciseRecordPythonDO exerciseRecord) {
        this.recordId = exerciseRecord.getId();
        this.choiceExerciseId = exerciseRecord.getExerciseId();
        this.questionText = exercise.getQuestionText();
        this.knowledgeConcept = knowledgeConcept;
        this.score = (int) (exerciseRecord.getScore() * 100);
        this.submitTime = exerciseRecord.getSubmitTime();
    }
}
