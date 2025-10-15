package com.springboot.cli.model.VO.python;

import com.springboot.cli.model.DO.KnowledgeEdgesDO;
import com.springboot.cli.model.DO.KnowledgeNodesDO;
import com.springboot.cli.model.DO.python.KnowledgeEdgesPythonDO;
import com.springboot.cli.model.DO.python.KnowledgeNodesPythonDO;
import lombok.Data;

import java.util.List;

/**
 * 前端展示用的学生知识图谱 VO
 */
@Data
public class StudentKnowledgeGraphPythonVO {

    private String studentId;

    private List<KnowledgeNodesPythonDO> nodes;

    private List<KnowledgeEdgesPythonDO> edges;

}
