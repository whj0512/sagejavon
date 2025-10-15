package com.springboot.cli.service.python;

import com.springboot.cli.model.VO.SMessageVO;

import java.util.List;

public interface TutorHistoryPythonService {
    Void buildMessage(List<SMessageVO> sMessageList);
}
