package com.springboot.cli.model.DO.python;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("review_python")
public class ReviewPythonDO {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String studentId;
    private Long exerciseId;
    private Integer review;
}
