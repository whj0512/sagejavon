package com.springboot.cli.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.springboot.cli.model.DO.python.ChatPythonDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChatPythonMapper extends BaseMapper<ChatPythonDO> {
}
