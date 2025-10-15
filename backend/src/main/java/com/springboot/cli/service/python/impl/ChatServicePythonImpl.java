package com.springboot.cli.service.python.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.springboot.cli.common.enums.OpExceptionEnum;
import com.springboot.cli.common.exception.OpException;
import com.springboot.cli.common.jwt.AuthStorage;
import com.springboot.cli.model.DO.python.ChatPythonDO;
import com.springboot.cli.model.DO.python.HistoryPythonDO;
import com.springboot.cli.model.VO.python.ChatPythonVO;
import com.springboot.cli.model.VO.python.ChatPythonVO;
import com.springboot.cli.repository.impl.ChatPythonRepository;
import com.springboot.cli.repository.impl.HistoryPythonRepository;
import com.springboot.cli.service.python.ChatPythonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChatServicePythonImpl implements ChatPythonService {
    @Autowired
    ChatPythonRepository chatRepository;

    @Autowired
    HistoryPythonRepository historyRepository;

    @Override
    public Long insert() {
        String studentId = AuthStorage.getUser().getUserId();
        ChatPythonDO chat = ChatPythonDO.builder()
                .studentId(studentId)
                .sort(0)
                .longTerm(0)
                .updateTime(LocalDateTime.now())
                .deletedFlag(0)
                .title("新建对话")
                .build();
        System.out.println(chat);
        chatRepository.save(chat);
        return chat.getId();
    }

    @Override
    public List<ChatPythonVO> getList() {
        String studentId = AuthStorage.getUser().getUserId();
        LambdaQueryWrapper<ChatPythonDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatPythonDO::getStudentId, studentId);
        queryWrapper.eq(ChatPythonDO::getDeletedFlag, 0);
        queryWrapper.orderByDesc(ChatPythonDO::getUpdateTime);
        List<ChatPythonDO> chatList = chatRepository.list(queryWrapper);
        List<ChatPythonVO> chatVOList = new ArrayList<>();
        chatList.forEach(chat -> chatVOList.add(new ChatPythonVO(chat)));
        return chatVOList;
    }

    @Override
    @Transactional
    public Void delete(Long chatId) {
        if(chatId == null) throw new OpException(OpExceptionEnum.ILLEGAL_ARGUMENT);
        UpdateWrapper<ChatPythonDO> chatUpdateWrapper = new UpdateWrapper<>();
        chatUpdateWrapper.eq("id", chatId)
                .set("deleted_flag", 1);
        chatRepository.update(chatUpdateWrapper);
        UpdateWrapper<HistoryPythonDO> historyQueryWrapper = new UpdateWrapper<>();
        historyQueryWrapper.eq("chat_id", chatId)
                .set("deleted_flag", 1);
        historyRepository.update(historyQueryWrapper);
        return null;
    }
}
