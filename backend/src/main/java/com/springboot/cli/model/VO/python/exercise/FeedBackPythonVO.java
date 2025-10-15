package com.springboot.cli.model.VO.python.exercise;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeedBackPythonVO {
    private Integer score;
    private String correctAnswer;
    private String suggestion;
}
