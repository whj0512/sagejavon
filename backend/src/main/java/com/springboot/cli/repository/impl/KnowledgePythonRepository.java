package com.springboot.cli.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.cli.mapper.KnowledgePythonMapper;
import com.springboot.cli.model.DO.python.KnowledgePythonDO;
import com.springboot.cli.model.VO.python.KnowledgeGraphPythonVO;
import com.springboot.cli.model.VO.python.exercise.KnowledgePythonVO;
import com.springboot.cli.repository.IKnowledgePythonRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class KnowledgePythonRepository extends ServiceImpl<KnowledgePythonMapper, KnowledgePythonDO> implements IKnowledgePythonRepo {

    @Autowired
    KnowledgePythonMapper knowledgeMapper;

    @Override
    public List<KnowledgeGraphPythonVO> getKnowledgeGraphVO(String studentId) {
        return knowledgeMapper.getKnowledgeGraphVO(studentId);
    }

    @Override
    public List<KnowledgePythonVO> getKnowledgeList(Long exerciseId) {
        return knowledgeMapper.getKnowledgeList(exerciseId);
    }
}
