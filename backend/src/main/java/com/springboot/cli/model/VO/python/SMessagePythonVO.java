package com.springboot.cli.model.VO.python;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SMessagePythonVO {
    private Integer role;
    private String content;
}
