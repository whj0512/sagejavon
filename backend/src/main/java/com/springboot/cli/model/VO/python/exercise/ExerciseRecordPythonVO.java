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
public class ExerciseRecordPythonVO {
    private Long recordId;
    private Long exerciseId;
    private String questionText;
    private List<KnowledgePythonVO> knowledgeConcept;
    private Integer score;
    private LocalDateTime submitTime;
    private Integer difficulty;
    private Integer type;
    private String chapter;

    public ExerciseRecordPythonVO(ExercisePythonDO exercise, List<KnowledgePythonVO> knowledgeConcept, ExerciseRecordPythonDO exerciseRecord) {
        this.recordId = exerciseRecord.getId();
        this.exerciseId = exercise.getId();
        this.questionText = exercise.getQuestionText();
        this.knowledgeConcept = knowledgeConcept;
        this.score = (exercise.getType() == 0) ? (int) (exerciseRecord.getScore() * 100) : (int) exerciseRecord.getScore().doubleValue();
        this.submitTime = exerciseRecord.getSubmitTime();
        this.difficulty = exercise.getDifficulty();
        this.type = exercise.getType();
        this.chapter = exercise.getChapter();
    }
}
