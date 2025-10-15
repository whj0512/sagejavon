package com.springboot.cli.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.cli.mapper.ChatPythonMapper;
import com.springboot.cli.model.DO.python.ChatPythonDO;
import com.springboot.cli.repository.IChatPythonRepo;
import org.springframework.stereotype.Service;

@Service
public class ChatPythonRepository extends ServiceImpl<ChatPythonMapper, ChatPythonDO> implements IChatPythonRepo {
}
