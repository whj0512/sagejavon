package com.springboot.cli.service;

import com.springboot.cli.model.DO.CheckCode;
import com.springboot.cli.model.DO.StudentDO;
import com.springboot.cli.model.VO.StudentKnowledgeGraphVO;
import com.springboot.cli.model.VO.StudentVO;

public interface StudentService {
    StudentDO getStuInfo();

    void updateStuInfo(StudentDO student);

    void registerStu(StudentDO student);

    StudentVO login(StudentDO studentDO);

    StudentKnowledgeGraphVO getPersonalGraph(StudentDO studentDO);

    Void checkCode(CheckCode checkCode);
}
