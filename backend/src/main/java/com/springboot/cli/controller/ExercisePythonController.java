package com.springboot.cli.controller;

import com.springboot.cli.common.base.BaseResponse;
import com.springboot.cli.common.enums.OpExceptionEnum;
import com.springboot.cli.common.exception.OpException;
import com.springboot.cli.common.jwt.AuthStorage;
import com.springboot.cli.model.DO.python.ExercisePythonDO;
import com.springboot.cli.model.DO.python.ExerciseRecordPythonDO;
import com.springboot.cli.model.DO.python.KnowledgePythonDO;
import com.springboot.cli.model.VO.python.exercise.*;
import com.springboot.cli.service.python.ExercisePythonService;
import com.springboot.cli.service.python.ExerciseRecordPythonService;
import com.springboot.cli.service.python.KnowledgePythonService;
import com.springboot.cli.service.python.ReviewPythonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/python/question")
public class ExercisePythonController {
    @Autowired
    private ExercisePythonService exerciseService;

    @Autowired
    private KnowledgePythonService knowledgeService;

    @Autowired
    private ExerciseRecordPythonService exerciseRecordService;

    @Autowired
    private ReviewPythonService reviewService;

    @GetMapping("/recommend")
    public BaseResponse<List<ExercisePythonVO>> getRecList(Integer questionNum, Integer difficultyOrder) {
        if(questionNum == null || difficultyOrder == null) return BaseResponse.buildBizEx(OpExceptionEnum.ILLEGAL_ARGUMENT);
        try {
            List<ExercisePythonDO> exerciseList = exerciseService.getRecList(AuthStorage.getUser().getUserId(), questionNum);
            if(exerciseList == null || exerciseList.isEmpty())
                return BaseResponse.buildSuccess(null);
            List<ExercisePythonVO> resultList = new ArrayList<>();
            for (ExercisePythonDO exercise : exerciseList) {
                List<KnowledgePythonVO> knowledgeList = knowledgeService.getKnowledgeList(exercise.getId());
                Integer done = exerciseRecordService.hasDoneExercise(AuthStorage.getUser().getUserId(), exercise.getId());
                ExercisePythonVO exerciseVO = new ExercisePythonVO(exercise, knowledgeList, done);
                resultList.add(exerciseVO);
            }
            if (difficultyOrder == 1)
                resultList.sort(Comparator.comparingInt(ExercisePythonVO::getDifficulty));
            if (difficultyOrder == 2)
                resultList.sort((o1, o2) -> Integer.compare(o2.getDifficulty(), o1.getDifficulty()));
            return BaseResponse.buildSuccess(resultList);
        } catch (OpException e) {
            return BaseResponse.buildBizEx(e);
        }
    }

    @GetMapping("/record/list")
    public BaseResponse<ExerciseRecordVOPythonPage> getRecordList(@RequestParam(defaultValue = "10") Integer pageSize, @RequestParam(defaultValue = "1") Integer pageNum) {
        if (pageSize == null || pageNum == null || pageNum < 1 || pageSize < 0)
            return BaseResponse.buildBizEx(OpExceptionEnum.ILLEGAL_ARGUMENT);
        try {
            ExerciseRecordPythonPage exerciseRecordPage = exerciseRecordService.page(pageSize, pageNum, AuthStorage.getUser().getUserId());
            List<ExerciseRecordPythonDO> exerciseRecordList = exerciseRecordPage.getExerciseRecordList();
            if(exerciseRecordList == null || exerciseRecordList.isEmpty())
                return BaseResponse.buildSuccess(null);
            List<ExerciseRecordPythonVO> resultList = new ArrayList<>();
            for (ExerciseRecordPythonDO exerciseRecord : exerciseRecordList) {
                List<KnowledgePythonVO> knowledgeList = knowledgeService.getKnowledgeList(exerciseRecord.getExerciseId());
                ExercisePythonDO exercise = exerciseService.getExerciseById(exerciseRecord.getExerciseId());
                resultList.add(new ExerciseRecordPythonVO(exercise, knowledgeList, exerciseRecord));
            }
            return BaseResponse.buildSuccess(new ExerciseRecordVOPythonPage(resultList, exerciseRecordPage.getTotal(), exerciseRecordPage.getPages()));
        } catch (OpException e) {
            return BaseResponse.buildBizEx(e);
        }
    }

    @GetMapping("/list")
    public BaseResponse<ExerciseVOPythonPage> getExerciseList(Integer type, @RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize, Integer difficulty, String knowledgeId, Integer difficultyOrder, String chapter ) {
        try {
            List<Long> knowledgeIdList = null;
            if (knowledgeId != null && !knowledgeId.isEmpty())
                knowledgeIdList = Arrays.stream(knowledgeId.split(",")).map(Long::parseLong).collect(Collectors.toList());
            if (pageSize == null || pageNum == null || pageNum < 1 || pageSize < 0 || difficultyOrder == null)
                return BaseResponse.buildBizEx(OpExceptionEnum.ILLEGAL_ARGUMENT);
            ExercisePythonPage exercisePage = exerciseService.page(type, pageNum, pageSize, difficulty, knowledgeIdList, difficultyOrder, chapter);
            List<ExercisePythonDO> exerciseList = exercisePage.getExerciseList();
            if(exerciseList == null || exerciseList.isEmpty())
                return BaseResponse.buildSuccess(null);
            List<ExercisePythonVO> resultList = new ArrayList<>();
            for (ExercisePythonDO exercise : exerciseList) {
                List<KnowledgePythonVO> knowledgeList = knowledgeService.getKnowledgeList(exercise.getId());
                Integer done = exerciseRecordService.hasDoneExercise(AuthStorage.getUser().getUserId(), exercise.getId());
                ExercisePythonVO exerciseVO = new ExercisePythonVO(exercise, knowledgeList, done);
                resultList.add(exerciseVO);
            }
            return BaseResponse.buildSuccess(new ExerciseVOPythonPage(resultList, exercisePage.getTotal(), exercisePage.getPages()));
        } catch (OpException e) {
            return BaseResponse.buildBizEx(e);
        }
    }

    @GetMapping("/knowledge")
    public BaseResponse<List<KnowledgePythonVO>> getKnowledge() {
        try {
            List<KnowledgePythonDO> knowledgeList = knowledgeService.getKnowledgeList();
            List<KnowledgePythonVO> resultList = new ArrayList<>();
            knowledgeList.forEach(knowledge -> resultList.add(new KnowledgePythonVO(knowledge)));
            return BaseResponse.buildSuccess(resultList);
        } catch (OpException e) {
            return BaseResponse.buildBizEx(e);
        }
    }

    @PostMapping("/review")
    public BaseResponse<Integer> review(Long exerciseId, Integer review) {
        try {
            return BaseResponse.buildSuccess(reviewService.review(exerciseId, review));
        } catch (OpException e) {
            return BaseResponse.buildBizEx(e);
        }
    }
}
