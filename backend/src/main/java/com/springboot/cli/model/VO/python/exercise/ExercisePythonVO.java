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
public class ExercisePythonVO {
    private Long id;
    private String questionText;
    private List<KnowledgePythonVO> knowledgeConcept;
    private Integer difficulty;
    private Integer done;
    private Integer type;
    private String chapter;

    public ExercisePythonVO(ExercisePythonDO exercise, List<KnowledgePythonVO> knowledgeList, Integer done) {
        this.id = exercise.getId();
        this.questionText = exercise.getQuestionText();
        this.knowledgeConcept = knowledgeList;
        this.difficulty = exercise.getDifficulty();
        this.done = done;
        this.type = exercise.getType();
        this.chapter = exercise.getChapter();
    }
}
