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
public class CodeExerciseRecordPythonVO {
    private Long recordId;
    private Long exerciseId;
    private String questionText;
    private List<KnowledgePythonVO> knowledgeConcept;
    private Integer score;
    private LocalDateTime submitTime;
    private Integer difficulty;
    private String chapter;

    public CodeExerciseRecordPythonVO(ExercisePythonDO exercise, List<KnowledgePythonVO> knowledgeConcept, ExerciseRecordPythonDO exerciseRecord) {
        this.recordId = exerciseRecord.getId();
        this.exerciseId = exerciseRecord.getId();
        this.questionText = exercise.getQuestionText();
        this.knowledgeConcept = knowledgeConcept;
        this.score = (int) (exerciseRecord.getScore() * 100);
        this.submitTime = exerciseRecord.getSubmitTime();
        this.difficulty = exercise.getDifficulty();
        this.chapter = exercise.getChapter();
    }
}
