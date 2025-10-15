package com.springboot.cli.controller;

import com.springboot.cli.common.base.BaseResponse;
import com.springboot.cli.common.exception.OpException;
import com.springboot.cli.model.VO.python.knowledge.KnowledgeGraphPythonVO;
import com.springboot.cli.service.python.KnowledgeGraphPythonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class KnowledgeGraphPythonController {
    @Autowired
    private KnowledgeGraphPythonService knowledgeGraphService;

    @GetMapping("/python/graph")
    public BaseResponse<List<KnowledgeGraphPythonVO>> getKnowledgeGraph(String studentId) {
        try {
            return BaseResponse.buildSuccess(knowledgeGraphService.getKnowledgeGraph(studentId));
        } catch (OpException e) {
            return BaseResponse.buildBizEx(e);
        } catch (Exception e) {
            return BaseResponse.buildSysEx(e);
        }
    }

    @PostMapping("/python/graph")
    public BaseResponse<String> update(@RequestBody String studentId, String query) {
        try {
            return BaseResponse.buildSuccess(knowledgeGraphService.update(studentId, query));
        } catch (OpException e) {
            return BaseResponse.buildBizEx(e);
        } catch (Exception e) {
            return BaseResponse.buildSysEx(e);
        }
    }
}
