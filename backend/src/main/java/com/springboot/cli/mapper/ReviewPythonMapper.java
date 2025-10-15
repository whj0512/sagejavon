package com.springboot.cli.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.springboot.cli.model.DO.python.ReviewPythonDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReviewPythonMapper extends BaseMapper<ReviewPythonDO> {
}
