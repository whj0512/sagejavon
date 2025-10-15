package com.springboot.cli.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.cli.mapper.KnowledgeEdgesPythonMapper;
import com.springboot.cli.model.DO.python.KnowledgeEdgesPythonDO;
import com.springboot.cli.repository.IKnowledgeEdgesPythonRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KnowledgeEdgesPythonRepository extends ServiceImpl<KnowledgeEdgesPythonMapper, KnowledgeEdgesPythonDO>
        implements IKnowledgeEdgesPythonRepo {

    public List<KnowledgeEdgesPythonDO> selectByStudentId(String studentId) {
        return this.lambdaQuery()
                .eq(KnowledgeEdgesPythonDO::getStudentId, studentId)
                .list();
    }
}

