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
public class SelectExercisePythonVO {
    private Long id;
    private String questionText;
    private List<KnowledgePythonVO> knowledgeConcept;
    private String choiceA;
    private String choiceB;
    private String choiceC;
    private String choiceD;
    private Integer difficulty;
    private Integer review;
    private String chapter;

    public SelectExercisePythonVO(ExercisePythonDO selectExercise, List<KnowledgePythonVO> knowledgeList, Integer review) {
        this.id = selectExercise.getId();
        this.questionText = selectExercise.getQuestionText();
        this.knowledgeConcept = knowledgeList;
        this.choiceA = selectExercise.getChoiceA();
        this.choiceB = selectExercise.getChoiceB();
        this.choiceC = selectExercise.getChoiceC();
        this.choiceD = selectExercise.getChoiceD();
        this.difficulty = selectExercise.getDifficulty();
        this.review = review;
        this.chapter = selectExercise.getChapter();
    }
}
