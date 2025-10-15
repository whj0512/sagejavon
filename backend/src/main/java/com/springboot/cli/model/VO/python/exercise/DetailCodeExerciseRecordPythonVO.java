package com.springboot.cli.model.VO.python.exercise;

import com.springboot.cli.model.DO.ExerciseDO;
import com.springboot.cli.model.DO.ExerciseRecordDO;
import com.springboot.cli.model.DO.python.ExercisePythonDO;
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
public class DetailCodeExerciseRecordPythonVO {
    private Long questionId;
    private String questionText;
    private List<KnowledgePythonVO> knowledgeConcept;
    private String code;
    private Integer score;
    private String correctAnswer;
    private String suggestion;
    private Integer difficulty;
    private String chapter;

    public DetailCodeExerciseRecordPythonVO(ExercisePythonDO exercise, List<KnowledgePythonVO> knowledgeConcept, ExerciseRecordPythonDO exerciseRecord) {
        this.questionId = exercise.getId();
        this.questionText = exercise.getQuestionText();
        this.knowledgeConcept = knowledgeConcept;
        this.code = exerciseRecord.getAnswer();
        this.score = (int) (exerciseRecord.getScore() * 100);
        this.correctAnswer = exercise.getCorrectAnswer();
        this.suggestion = exerciseRecord.getSuggestion();
        this.difficulty = exercise.getDifficulty();
        this.chapter = exercise.getChapter();
    }
}
