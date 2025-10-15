package com.springboot.cli.controller;

import com.springboot.cli.common.base.BaseResponse;
import com.springboot.cli.common.exception.OpException;
import com.springboot.cli.common.jwt.AuthStorage;
import com.springboot.cli.model.DO.python.ExercisePythonDO;
import com.springboot.cli.model.DO.python.ExerciseRecordPythonDO;
import com.springboot.cli.model.VO.python.exercise.DetailSelectExerciseRecordPythonVO;
import com.springboot.cli.model.VO.python.exercise.FeedBackPythonVO;
import com.springboot.cli.model.VO.python.exercise.KnowledgePythonVO;
import com.springboot.cli.model.VO.python.exercise.SelectExercisePythonVO;
import com.springboot.cli.service.python.ExercisePythonService;
import com.springboot.cli.service.python.ExerciseRecordPythonService;
import com.springboot.cli.service.python.KnowledgePythonService;
import com.springboot.cli.service.python.ReviewPythonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.springboot.cli.common.enums.OpExceptionEnum.ILLEGAL_ARGUMENT;

@RestController
@Slf4j
@RequestMapping("/python/question/select")
public class SelectExercisePythonController {
    @Autowired
    private ExercisePythonService exerciseService;

    @Autowired
    private KnowledgePythonService knowledgeService;

    @Autowired
    private ExerciseRecordPythonService exerciseRecordService;

    @Autowired
    private ReviewPythonService reviewService;

    @GetMapping("/detail")
    public BaseResponse<SelectExercisePythonVO> getExerciseDetail(Long id) {
        if(id == null) return BaseResponse.buildBizEx(ILLEGAL_ARGUMENT);
        try {
            ExercisePythonDO exercise = exerciseService.getExerciseById(id);
            if(exercise == null)
                return BaseResponse.buildSuccess(null);
            List<KnowledgePythonVO> knowledgeList = knowledgeService.getKnowledgeList(id);
            Integer review = reviewService.getReview(AuthStorage.getUser().getUserId(), id);
            SelectExercisePythonVO selectExercise = new SelectExercisePythonVO(exercise, knowledgeList, review);
            return BaseResponse.buildSuccess(selectExercise);
        } catch (OpException e) {
            return BaseResponse.buildBizEx(e);
        }
    }

    @PostMapping
    public BaseResponse<FeedBackPythonVO> getFeedBack(Long id, String choice) {
        if(id == null || choice == null) return BaseResponse.buildBizEx(ILLEGAL_ARGUMENT);
        try {
            FeedBackPythonVO feedBack = exerciseService.getSelectFeedBack(id, choice);
            return BaseResponse.buildSuccess(feedBack);
        } catch (OpException e) {
            return BaseResponse.buildBizEx(e);
        }
    }

    @GetMapping("/record/detail")
    public BaseResponse<DetailSelectExerciseRecordPythonVO> getDetailExerciseRecord(Long recordId) {
        if(recordId == null) return BaseResponse.buildBizEx(ILLEGAL_ARGUMENT);
        try {
            ExerciseRecordPythonDO exerciseRecord = exerciseRecordService.getExerciseRecord(recordId);
            if(exerciseRecord == null) return BaseResponse.buildBizEx(ILLEGAL_ARGUMENT);
            ExercisePythonDO exercise = exerciseService.getExerciseById(exerciseRecord.getExerciseId());
            List<KnowledgePythonVO> knowledgeList = knowledgeService.getKnowledgeList(exerciseRecord.getExerciseId());
            return BaseResponse.buildSuccess(new DetailSelectExerciseRecordPythonVO(exercise, knowledgeList, exerciseRecord));
        } catch (OpException e) {
            return BaseResponse.buildBizEx(e);
        }
    }
}
