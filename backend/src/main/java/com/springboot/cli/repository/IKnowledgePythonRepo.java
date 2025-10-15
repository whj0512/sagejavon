package com.springboot.cli.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.springboot.cli.model.DO.python.KnowledgePythonDO;
import com.springboot.cli.model.VO.python.KnowledgeGraphPythonVO;
import com.springboot.cli.model.VO.python.exercise.KnowledgePythonVO;

import java.util.List;

public interface IKnowledgePythonRepo extends IService<KnowledgePythonDO> {
    List<KnowledgeGraphPythonVO> getKnowledgeGraphVO(String studentId);

    List<KnowledgePythonVO> getKnowledgeList(Long exerciseId);
}
