package com.springboot.cli.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.cli.mapper.KnowledgeNodesPythonMapper;
import com.springboot.cli.model.DO.python.KnowledgeNodesPythonDO;
import com.springboot.cli.repository.IKnowledgeNodesPythonRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KnowledgeNodesPythonRepository extends ServiceImpl<KnowledgeNodesPythonMapper, KnowledgeNodesPythonDO>
        implements IKnowledgeNodesPythonRepo {

    public List<KnowledgeNodesPythonDO> selectByStudentId(String studentId) {
        return this.lambdaQuery()
                .eq(KnowledgeNodesPythonDO::getStudentId, studentId)
                .list();
    }
}
