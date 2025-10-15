package com.springboot.cli.service.python.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.springboot.cli.common.enums.OpExceptionEnum;
import com.springboot.cli.common.exception.OpException;
import com.springboot.cli.model.DO.ChatDO;
import com.springboot.cli.model.DO.HistoryDO;
import com.springboot.cli.model.DO.python.ChatPythonDO;
import com.springboot.cli.model.DO.python.HistoryPythonDO;
import com.springboot.cli.model.VO.MessageVO;
import com.springboot.cli.model.VO.SMessageVO;
import com.springboot.cli.model.VO.python.MessagePythonVO;
import com.springboot.cli.repository.impl.ChatRepository;
import com.springboot.cli.repository.impl.HistoryRepository;
import com.springboot.cli.repository.impl.ChatPythonRepository;
import com.springboot.cli.repository.impl.HistoryPythonRepository;
import com.springboot.cli.service.HistoryService;
import com.springboot.cli.service.python.HistoryPythonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class HistoryServicePythonImpl implements HistoryPythonService {
    @Autowired
    private ChatPythonRepository chatRepository;

    @Autowired
    private HistoryPythonRepository historyRepository;

    @Override
    @Transactional
    public List<MessagePythonVO> buildMessage(Long chatId, List<SMessageVO> sMessageList) {
        if (chatId == null) throw new OpException(OpExceptionEnum.ILLEGAL_ARGUMENT);
        ChatPythonDO chat = chatRepository.getById(chatId);
        if(chat == null) throw new OpException(OpExceptionEnum.ILLEGAL_ARGUMENT);
        int sort = chat.getSort();
        List<HistoryPythonDO> batchList = new ArrayList<>();
        for(SMessageVO sMessage : sMessageList) {
            sort++;
            HistoryPythonDO history = HistoryPythonDO.builder()
                    .role(sMessage.getRole())
                    .chatId(chatId)
                    .content(sMessage.getContent())
                    .timeStamp(LocalDateTime.now())
                    .sort(sort)
                    .deletedFlag(0)
                    .build();
            batchList.add(history);
        }
        historyRepository.saveBatch(batchList);
        //chat
        chat.setSort(sort);
        chat.setUpdateTime(LocalDateTime.now());
        chatRepository.updateById(chat);
        return getMessageVOList(chatId);
    }

    @Override
    public List<MessagePythonVO> getHistory(Long chatId) {
        if (chatId == null) throw new OpException(OpExceptionEnum.ILLEGAL_ARGUMENT);
        return getMessageVOList(chatId);
    }

    private List<MessagePythonVO> getMessageVOList(Long chatId) {
        LambdaQueryWrapper<HistoryPythonDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HistoryPythonDO::getChatId, chatId);
        queryWrapper.eq(HistoryPythonDO::getDeletedFlag, 0);
        queryWrapper.orderByAsc(HistoryPythonDO::getSort);
        List<HistoryPythonDO> historyList = historyRepository.list(queryWrapper);
        List<MessagePythonVO> messageList = new ArrayList<>();
        historyList.forEach(history -> messageList.add(new MessagePythonVO(history)));
        return messageList;
    }
}
