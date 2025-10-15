package com.springboot.cli.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.cli.mapper.ExercisePythonMapper;
import com.springboot.cli.model.DO.python.ExercisePythonDO;
import com.springboot.cli.repository.IExercisePythonRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ExercisePythonRepository extends ServiceImpl<ExercisePythonMapper, ExercisePythonDO> implements IExercisePythonRepo {
    @Autowired
    ExercisePythonMapper exerciseMapper;

    @Override
    public Integer getExerciseNumber(int type) {
        return exerciseMapper.getExerciseNumber(type);
    }
}
