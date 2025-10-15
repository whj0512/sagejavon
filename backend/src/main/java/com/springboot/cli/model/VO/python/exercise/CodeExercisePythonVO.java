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
public class CodeExercisePythonVO {
    private Long id;
    private String questionText;
    private List<KnowledgePythonVO> knowledgeConcept;
    private Integer difficulty;
    private Integer done;
    private Integer review;
    private String chapter;

    public CodeExercisePythonVO(ExercisePythonDO codeExercise, List<KnowledgePythonVO> knowledgeList, Integer done, Integer review) {
        this.id = codeExercise.getId();
        this.questionText = codeExercise.getQuestionText();
        this.knowledgeConcept = knowledgeList;
        this.difficulty = codeExercise.getDifficulty();
        this.done = done;
        this.review = review;
        this.chapter = codeExercise.getChapter();
    }
}
