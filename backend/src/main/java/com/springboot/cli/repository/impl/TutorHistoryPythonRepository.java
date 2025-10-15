package com.springboot.cli.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.cli.mapper.TutorHistoryPythonMapper;
import com.springboot.cli.model.DO.python.TutorHistoryPythonDO;
import com.springboot.cli.repository.ITutorHistoryPythonRepo;
import org.springframework.stereotype.Service;

@Service
public class TutorHistoryPythonRepository extends ServiceImpl<TutorHistoryPythonMapper, TutorHistoryPythonDO> implements ITutorHistoryPythonRepo {
}
