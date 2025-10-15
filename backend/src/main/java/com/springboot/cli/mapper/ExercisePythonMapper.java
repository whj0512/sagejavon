package com.springboot.cli.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.springboot.cli.model.DO.python.ExercisePythonDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ExercisePythonMapper extends BaseMapper<ExercisePythonDO> {
    @Select("select count(*) from exercise_python where type=#{type}")
    Integer getExerciseNumber(int type);
}
