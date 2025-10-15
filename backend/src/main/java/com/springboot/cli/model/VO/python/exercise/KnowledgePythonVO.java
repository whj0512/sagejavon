package com.springboot.cli.model.VO.python.exercise;

import com.springboot.cli.model.DO.KnowledgeDO;
import com.springboot.cli.model.DO.python.KnowledgePythonDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KnowledgePythonVO {
    private Long knowledgeId;
    private String knowledge;

    public KnowledgePythonVO(KnowledgePythonDO knowledgeDO) {
        this.knowledgeId = knowledgeDO.getId();
        this.knowledge = knowledgeDO.getKnowledge();
    }
}
