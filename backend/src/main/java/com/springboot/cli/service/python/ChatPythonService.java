package com.springboot.cli.service.python;

import com.springboot.cli.model.VO.python.ChatPythonVO;

import java.util.List;

public interface ChatPythonService {
    Long insert();

    List<ChatPythonVO> getList();

    Void delete(Long chatId);
}
