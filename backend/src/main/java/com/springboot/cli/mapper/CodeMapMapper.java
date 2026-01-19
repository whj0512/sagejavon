package com.springboot.cli.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.springboot.cli.model.DO.CodeMapDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CodeMapMapper extends BaseMapper<CodeMapDO> {
}
