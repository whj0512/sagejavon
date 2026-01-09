package com.springboot.cli.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.cli.mapper.ChatMapper;
import com.springboot.cli.mapper.CodeMapMapper;
import com.springboot.cli.model.DO.ChatDO;
import com.springboot.cli.model.DO.CodeMapDO;
import com.springboot.cli.repository.IChatRepo;
import com.springboot.cli.repository.ICodeMapRepo;
import org.springframework.stereotype.Service;

@Service
public class CodeMapRepository extends ServiceImpl<CodeMapMapper, CodeMapDO> implements ICodeMapRepo {
}
