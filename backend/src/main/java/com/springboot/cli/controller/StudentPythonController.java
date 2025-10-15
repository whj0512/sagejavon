package com.springboot.cli.controller;

import com.springboot.cli.common.base.BaseResponse;
import com.springboot.cli.common.exception.OpException;
import com.springboot.cli.common.jwt.AuthStorage;
import com.springboot.cli.common.jwt.JwtUser;
import com.springboot.cli.model.DO.StudentDO;
import com.springboot.cli.model.VO.StudentVO;
import com.springboot.cli.model.VO.python.StudentKnowledgeGraphPythonVO;
import com.springboot.cli.service.python.StudentPythonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j

public class StudentPythonController {

    @Autowired
    private StudentPythonService studentService;

    @GetMapping("/python/information")
    public BaseResponse<StudentDO> getStuInfo() {
        JwtUser jwtUser = AuthStorage.getUser();
        log.info("Get student information: studentId = {}", jwtUser.getUserId());
        try {
            return BaseResponse.buildSuccess(studentService.getStuInfo());
        } catch (OpException e) {
            return BaseResponse.buildBizEx(e);
        } catch (Exception e) {
            return BaseResponse.buildSysEx(e);
        }
    }

    @PostMapping("/python/information")
    public BaseResponse<Void> updateStuInfo(@RequestBody StudentDO student) {
        JwtUser jwtUser = AuthStorage.getUser();
        log.info("Update student information: studentId = {}", jwtUser.getUserId());
        try {
            studentService.updateStuInfo(student);
            return BaseResponse.buildSuccess(null);
        } catch (OpException e) {
            return BaseResponse.buildBizEx(e);
        } catch (Exception e) {
            return BaseResponse.buildSysEx(e);
        }
    }

    @PostMapping("/python/register")
    public BaseResponse<Void> registerStu(@RequestBody StudentDO studentDO) {
        log.info("register a new student: studentName = {}", studentDO.getNickname());
        try {
            studentService.registerStu(studentDO);
            return BaseResponse.buildSuccess(null);
        } catch (OpException e) {
            return BaseResponse.buildBizEx(e);
        } catch (Exception e) {
            return BaseResponse.buildSysEx(e);
        }
    }

    @PostMapping("/python/login")
    public BaseResponse<StudentVO> loginStu(@RequestBody StudentDO studentDO) {
        log.info("Login student information: studentPhone = {}", studentDO.getPhone());
        try {
            StudentVO studentVO = studentService.login(studentDO);
            return BaseResponse.buildSuccess(studentVO);
        } catch (OpException e) {
            return BaseResponse.buildBizEx(e);
        } catch (Exception e){
            return BaseResponse.buildSysEx(e);
        }
    }

    @PostMapping("/python/personal/graph")
    public BaseResponse<StudentKnowledgeGraphPythonVO> getPersonalGraph(@RequestBody StudentDO studentDO) {
        log.info("获取学生知识图谱: studentId = {}", studentDO.getId());

        try {
            StudentKnowledgeGraphPythonVO knowledgeGraphVO = studentService.getPersonalGraph(studentDO);
            return BaseResponse.buildSuccess(knowledgeGraphVO);
        } catch (OpException e) {
            return BaseResponse.buildBizEx(e);
        } catch (Exception e) {
            log.error("查询知识图谱失败", e);
            return BaseResponse.buildSysEx(e);
        }
    }


//    @PostMapping("/personal/graph/update")
//    public BaseResponse<StudentKnowledgeGraphVO> updatePersonalGraph(@RequestBody String knowledge) {
//
//        try {
//
//            return BaseResponse.buildSuccess(knowledgeGraphVO);
//        } catch (OpException e) {
//            return BaseResponse.buildBizEx(e);
//        } catch (Exception e) {
//            log.error("查询知识图谱失败", e);
//            return BaseResponse.buildSysEx(e);
//        }
//    }
}
