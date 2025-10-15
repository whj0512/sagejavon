package com.springboot.cli.service.python;

import com.springboot.cli.model.VO.MessageVO;
import com.springboot.cli.model.VO.SMessageVO;
import com.springboot.cli.model.VO.python.MessagePythonVO;

import java.util.List;

public interface HistoryPythonService {
    List<MessagePythonVO> buildMessage(Long chatId, List<SMessageVO> messageList);

    List<MessagePythonVO> getHistory(Long chatId);
}
