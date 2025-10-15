package com.springboot.cli.model.VO.python;

import com.springboot.cli.model.DO.ChatDO;
import com.springboot.cli.model.DO.python.ChatPythonDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatPythonVO {
    private Long chatId;
    private String title;
    private Integer longTerm;
    private LocalDateTime updateTime;

    public ChatPythonVO(ChatPythonDO chat) {
        this.chatId = chat.getId();
        this.title = chat.getTitle();
        this.longTerm = chat.getLongTerm();
        this.updateTime = chat.getUpdateTime();
    }
}
