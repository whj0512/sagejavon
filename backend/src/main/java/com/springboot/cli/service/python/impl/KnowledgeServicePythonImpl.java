package com.springboot.cli.service.python.impl;

import com.springboot.cli.common.enums.OpExceptionEnum;
import com.springboot.cli.common.exception.OpException;
import com.springboot.cli.model.DO.KnowledgeDO;
import com.springboot.cli.model.DO.python.KnowledgePythonDO;
import com.springboot.cli.model.VO.KnowledgeGraphVO;
import com.springboot.cli.model.VO.exercise.KnowledgeVO;
import com.springboot.cli.model.VO.python.KnowledgeGraphPythonVO;
import com.springboot.cli.model.VO.python.exercise.KnowledgePythonVO;
import com.springboot.cli.repository.impl.ExerciseKnowledgeRepository;
import com.springboot.cli.repository.impl.KnowledgeRepository;
import com.springboot.cli.repository.impl.ExerciseKnowledgePythonRepository;
import com.springboot.cli.repository.impl.KnowledgePythonRepository;
import com.springboot.cli.service.KnowledgeService;
import com.springboot.cli.service.python.KnowledgePythonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class KnowledgeServicePythonImpl implements KnowledgePythonService {
    @Autowired
    KnowledgePythonRepository knowledgeRepository;

    @Autowired
    ExerciseKnowledgePythonRepository exerciseKnowledgeRepository;

    @Override
    public void save(List<KnowledgePythonDO> modelList) {
        modelList.forEach(model -> knowledgeRepository.saveOrUpdate(model));
    }

    @Override
    public List<KnowledgeGraphPythonVO> get(String studentId) {
        if(studentId == null) throw new OpException(OpExceptionEnum.USER_ID_EMPTY);
        List<KnowledgeGraphPythonVO> knowledgeGraph = knowledgeRepository.getKnowledgeGraphVO(studentId);
        if(knowledgeGraph != null)
            knowledgeGraph.forEach(knowledge -> {
                if(knowledge.getQuery() == null) knowledge.setQuery(0);
                if(knowledge.getLevel() == null) knowledge.setLevel(0);
            });
        return knowledgeGraph;
    }

    @Override
    public List<KnowledgePythonVO> getKnowledgeList(Long exerciseId) {
        return knowledgeRepository.getKnowledgeList(exerciseId);
    }

    @Override
    public List<KnowledgePythonDO> getKnowledgeList() {
        return knowledgeRepository.list();
    }
}
