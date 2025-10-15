package com.springboot.cli.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.cli.mapper.HistoryPythonMapper;
import com.springboot.cli.model.DO.python.HistoryPythonDO;
import com.springboot.cli.repository.IHistoryPythonRepo;
import org.springframework.stereotype.Service;

@Service
public class HistoryPythonRepository extends ServiceImpl<HistoryPythonMapper, HistoryPythonDO> implements IHistoryPythonRepo {
}
