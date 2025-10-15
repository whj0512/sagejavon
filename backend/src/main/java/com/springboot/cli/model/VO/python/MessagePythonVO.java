package com.springboot.cli.model.VO.python;

import com.springboot.cli.model.DO.HistoryDO;
import com.springboot.cli.model.DO.python.HistoryPythonDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessagePythonVO {
    private String content;
    private Integer role;
    private LocalDateTime timeStamp;
    private Integer sort;

    public MessagePythonVO(HistoryPythonDO history) {
        this.content = history.getContent();
        this.role = history.getRole();
        this.timeStamp = history.getTimeStamp();
        this.sort = history.getSort();
    }
}
