package com.springboot.cli.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.cli.mapper.ExerciseRecordPythonMapper;
import com.springboot.cli.model.DO.python.ExerciseRecordPythonDO;
import com.springboot.cli.repository.IExerciseRecordPythonRepo;
import org.springframework.stereotype.Service;

@Service
public class ExerciseRecordPythonRepository extends ServiceImpl<ExerciseRecordPythonMapper, ExerciseRecordPythonDO> implements IExerciseRecordPythonRepo {
}
