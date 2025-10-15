package com.springboot.cli.controller;

import com.springboot.cli.common.base.BaseResponse;
import com.springboot.cli.common.exception.OpException;
import com.springboot.cli.common.jwt.AuthStorage;
import com.springboot.cli.model.DO.python.ExercisePythonDO;
import com.springboot.cli.model.DO.python.ExerciseRecordPythonDO;
import com.springboot.cli.model.VO.python.exercise.*;
import com.springboot.cli.service.python.ExercisePythonService;
import com.springboot.cli.service.python.ExerciseRecordPythonService;
import com.springboot.cli.service.python.KnowledgePythonService;
import com.springboot.cli.service.python.ReviewPythonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static com.springboot.cli.common.enums.OpExceptionEnum.ILLEGAL_ARGUMENT;

@RestController
@Slf4j
@RequestMapping("/python/question/code")
public class CodeExercisePythonController {
    @Autowired
    private ExercisePythonService exerciseService;

    @Autowired
    private KnowledgePythonService knowledgeService;

    @Autowired
    private ExerciseRecordPythonService exerciseRecordService;

    @Autowired
    private ReviewPythonService reviewService;

    @GetMapping("/detail")
    public BaseResponse<CodeExercisePythonVO> getExerciseDetail(Long id) {
        try {
            ExercisePythonDO exercise = exerciseService.getExerciseById(id);
            if(exercise == null)
                return BaseResponse.buildSuccess(null);
            List<KnowledgePythonVO> knowledgeList = knowledgeService.getKnowledgeList(id);
            Integer done = exerciseRecordService.hasDoneExercise(AuthStorage.getUser().getUserId(), id);
            Integer review = reviewService.getReview(AuthStorage.getUser().getUserId(), id);
            CodeExercisePythonVO codeExercise = new CodeExercisePythonVO(exercise, knowledgeList, done, review);
            return BaseResponse.buildSuccess(codeExercise);
        } catch (OpException e) {
            return BaseResponse.buildBizEx(e);
        }
    }

    @PostMapping
    public BaseResponse<FeedBackPythonVO> getFeedBack(Long id, @RequestBody String answer, Integer submitNum) {
        if(id == null || answer == null || answer.isEmpty() || submitNum == null) return BaseResponse.buildBizEx(ILLEGAL_ARGUMENT);
        try {
            FeedBackPythonVO feedBack = exerciseService.getFeedBack(id, answer, submitNum);
            return BaseResponse.buildSuccess(feedBack);
        } catch (OpException e) {
            return BaseResponse.buildBizEx(e);
        }
    }

    @GetMapping("/record/list")
    public BaseResponse<List<CodeExerciseRecordPythonVO>> getExerciseRecordList(Long questionId) {
        if(questionId == null) return BaseResponse.buildBizEx(ILLEGAL_ARGUMENT);
        try {
            ExercisePythonDO exercise = exerciseService.getExerciseById(questionId);
            if(exercise == null) return BaseResponse.buildBizEx(ILLEGAL_ARGUMENT);
            List<KnowledgePythonVO> knowledgeList = knowledgeService.getKnowledgeList(questionId);
            List<ExerciseRecordPythonDO> exerciseRecordList = exerciseRecordService.getExerciseRecord(AuthStorage.getUser().getUserId(), questionId);
            List<CodeExerciseRecordPythonVO> resultList = new ArrayList<>();
            exerciseRecordList.forEach(record -> resultList.add(new CodeExerciseRecordPythonVO(exercise, knowledgeList, record)));
            return BaseResponse.buildSuccess(resultList);
        } catch (OpException e) {
            return BaseResponse.buildBizEx(e);
        }
    }

    @GetMapping("/record/detail")
    public BaseResponse<DetailCodeExerciseRecordPythonVO> getDetailExerciseRecord(Long recordId) {
        if(recordId == null) return BaseResponse.buildBizEx(ILLEGAL_ARGUMENT);
        try {
            ExerciseRecordPythonDO exerciseRecord = exerciseRecordService.getExerciseRecord(recordId);
            if(exerciseRecord == null) return BaseResponse.buildBizEx(ILLEGAL_ARGUMENT);
            ExercisePythonDO exercise = exerciseService.getExerciseById(exerciseRecord.getExerciseId());
            List<KnowledgePythonVO> knowledgeList = knowledgeService.getKnowledgeList(exerciseRecord.getExerciseId());
            return BaseResponse.buildSuccess(new DetailCodeExerciseRecordPythonVO(exercise, knowledgeList, exerciseRecord));
        } catch (OpException e) {
            return BaseResponse.buildBizEx(e);
        }
    }
}
