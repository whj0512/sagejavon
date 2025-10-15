package com.springboot.cli.service.python.impl;

import com.springboot.cli.common.jwt.AuthStorage;
import com.springboot.cli.model.DO.TutorHistoryDO;
import com.springboot.cli.model.DO.python.TutorHistoryPythonDO;
import com.springboot.cli.model.VO.SMessageVO;
import com.springboot.cli.repository.impl.TutorHistoryRepository;
import com.springboot.cli.repository.impl.TutorHistoryPythonRepository;
import com.springboot.cli.service.TutorHistoryService;
import com.springboot.cli.service.python.TutorHistoryPythonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TutorHistoryPythonServiceImpl implements TutorHistoryPythonService {
    @Autowired
    private TutorHistoryPythonRepository tutorHistoryRepository;

    @Override
    public Void buildMessage(List<SMessageVO> sMessageList) {
        List<TutorHistoryPythonDO> batchList = new ArrayList<>();
        for(SMessageVO sMessage : sMessageList) {
            TutorHistoryPythonDO history = TutorHistoryPythonDO.builder()
                    .studentId(AuthStorage.getUser().getUserId())
                    .role(sMessage.getRole())
                    .content(sMessage.getContent())
                    .timeStamp(LocalDateTime.now())
                    .build();
            batchList.add(history);
        }
        tutorHistoryRepository.saveBatch(batchList);
        return null;
    }
}
