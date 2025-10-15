package com.springboot.cli.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.cli.mapper.MasteryPythonMapper;
import com.springboot.cli.model.DO.python.MasteryPythonDO;
import com.springboot.cli.repository.IMasteryPythonRepo;
import org.springframework.stereotype.Service;

@Service
public class MasteryPythonRepository extends ServiceImpl<MasteryPythonMapper, MasteryPythonDO> implements IMasteryPythonRepo {
}
