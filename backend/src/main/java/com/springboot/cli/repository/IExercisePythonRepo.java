package com.springboot.cli.repository;


import com.baomidou.mybatisplus.extension.service.IService;
import com.springboot.cli.model.DO.python.ExercisePythonDO;

public interface IExercisePythonRepo extends IService<ExercisePythonDO> {
    Integer getExerciseNumber(int type);
}
