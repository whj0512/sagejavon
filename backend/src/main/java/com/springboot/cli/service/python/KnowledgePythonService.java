package com.springboot.cli.service.python;

import com.springboot.cli.model.DO.python.KnowledgePythonDO;
import com.springboot.cli.model.VO.KnowledgeGraphVO;
import com.springboot.cli.model.VO.exercise.KnowledgeVO;
import com.springboot.cli.model.VO.python.KnowledgeGraphPythonVO;
import com.springboot.cli.model.VO.python.exercise.KnowledgePythonVO;

import java.util.List;

public interface KnowledgePythonService {
    void save(List<KnowledgePythonDO> modelList);

    List<KnowledgeGraphPythonVO> get(String studentId);

    List<KnowledgePythonVO> getKnowledgeList(Long exerciseId);

    List<KnowledgePythonDO> getKnowledgeList();
}
