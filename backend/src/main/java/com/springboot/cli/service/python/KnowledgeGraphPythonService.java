package com.springboot.cli.service.python;

import com.springboot.cli.model.VO.python.knowledge.KnowledgeGraphPythonVO;

import java.util.List;

public interface KnowledgeGraphPythonService {

    List<KnowledgeGraphPythonVO> getKnowledgeGraph(String studentId);

    String update(String studentId, String query);
}
