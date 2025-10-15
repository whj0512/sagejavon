package com.springboot.cli.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.cli.mapper.ExerciseKnowledgePythonMapper;
import com.springboot.cli.model.DO.python.ExerciseKnowledgePythonDO;
import com.springboot.cli.repository.IExerciseKnowledgePythonRepo;
import org.springframework.stereotype.Service;

@Service
public class ExerciseKnowledgePythonRepository extends ServiceImpl<ExerciseKnowledgePythonMapper, ExerciseKnowledgePythonDO> implements IExerciseKnowledgePythonRepo {
}
