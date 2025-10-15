package com.springboot.cli.service.python;

import com.springboot.cli.model.DO.StudentDO;
import com.springboot.cli.model.VO.StudentKnowledgeGraphVO;
import com.springboot.cli.model.VO.StudentVO;
import com.springboot.cli.model.VO.python.StudentKnowledgeGraphPythonVO;

public interface StudentPythonService {
    StudentDO getStuInfo();

    void updateStuInfo(StudentDO student);

    void registerStu(StudentDO student);

    StudentVO login(StudentDO studentDO);

    StudentKnowledgeGraphPythonVO getPersonalGraph(StudentDO studentDO);
}
