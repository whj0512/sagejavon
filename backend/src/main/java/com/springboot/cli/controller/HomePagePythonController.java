package com.springboot.cli.controller;

import com.springboot.cli.common.base.BaseResponse;
import com.springboot.cli.common.exception.OpException;
import com.springboot.cli.common.jwt.AuthStorage;
import com.springboot.cli.model.DO.python.ExercisePythonDO;
import com.springboot.cli.model.DO.python.ExerciseRecordPythonDO;
import com.springboot.cli.model.VO.python.AllInformationPythonVO;
import com.springboot.cli.model.VO.python.exercise.ExercisePythonVO;
import com.springboot.cli.model.VO.python.exercise.KnowledgePythonVO;
import com.springboot.cli.service.python.ExercisePythonService;
import com.springboot.cli.service.python.ExerciseRecordPythonService;
import com.springboot.cli.service.python.KnowledgePythonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/python/homepage")
public class HomePagePythonController {

    @Autowired
    private ExercisePythonService exerciseService;

    @Autowired
    private ExerciseRecordPythonService exerciseRecordService;

    @Autowired
    private KnowledgePythonService knowledgeService;

    @GetMapping
    public BaseResponse<AllInformationPythonVO> getAllInformation() {
        try {
            Integer codeNumber = exerciseService.getExerciseLNumber(0);
            Integer selectNumber = exerciseService.getExerciseLNumber(1);
            List<ExerciseRecordPythonDO> allExerciseRecord = exerciseRecordService.getStudentAllExerciseRecord(AuthStorage.getUser().getUserId());
            LocalDateTime maxSubmitTime = getMaxSubmitTime(allExerciseRecord);

            List<Long> top10ExerciseIds = getTop10PopularExerciseIds(allExerciseRecord);
            List<ExercisePythonVO> exerciseVOList = new ArrayList<>();
            for (Long id : top10ExerciseIds) {
                ExercisePythonDO exercise = exerciseService.getExerciseById(id);
                List<KnowledgePythonVO> knowledgeList = knowledgeService.getKnowledgeList(id);
                Integer done = exerciseRecordService.hasDoneExercise(AuthStorage.getUser().getUserId(), id);
                ExercisePythonVO exerciseVO = new ExercisePythonVO(exercise, knowledgeList, done);
                exerciseVOList.add(exerciseVO);
            }

            Integer continuousSolveDays = getContinuousSolveDays(allExerciseRecord);
            Integer solveCount = getSolveCount(allExerciseRecord);
            Map<LocalDate, Integer> solvePerDay = getSolvePerDay(allExerciseRecord);

            return BaseResponse.buildSuccess(new AllInformationPythonVO(codeNumber,selectNumber,maxSubmitTime,exerciseVOList,continuousSolveDays,solveCount,solvePerDay));
        } catch (OpException e) {
            return BaseResponse.buildBizEx(e);
        }
    }

    public static LocalDateTime getMaxSubmitTime(List<ExerciseRecordPythonDO> allExercise) {
        return allExercise.stream()
                .map(ExerciseRecordPythonDO::getSubmitTime)
                .max(Comparator.naturalOrder())
                .orElse(null);
    }

    private Map<LocalDate, Integer> getSolvePerDay(List<ExerciseRecordPythonDO> allExercise) {
        // 获取当前日期
        LocalDate now = LocalDate.now();

        // 过滤出近一个月的做题记录
        List<ExerciseRecordPythonDO> recentExercises = allExercise.stream()
                .filter(record -> record.getSubmitTime().toLocalDate().isAfter(now.minusMonths(1)))
                .collect(Collectors.toList());

        // 统计每一天的做题数量
        Map<LocalDate, List<Long>> exerciseCountPerDay = new HashMap<>();
        for (ExerciseRecordPythonDO record : recentExercises) {
            LocalDate submitDate = record.getSubmitTime().toLocalDate();
            exerciseCountPerDay.computeIfAbsent(submitDate, k -> new ArrayList<>()).add(record.getExerciseId());
        }

        // 计算每一天的不同 exerciseId 的数量
        Map<LocalDate, Integer> solvePerDay = new HashMap<>();
        for (Map.Entry<LocalDate, List<Long>> entry : exerciseCountPerDay.entrySet()) {
            List<Long> exerciseIds = entry.getValue();
            int distinctExerciseCount = (int) exerciseIds.stream().distinct().count();
            solvePerDay.put(entry.getKey(), distinctExerciseCount);
        }
        return solvePerDay;
    }

    public int getContinuousSolveDays(List<ExerciseRecordPythonDO> allExercise) {
        // 获取当前日期
        LocalDate now = LocalDate.now();

        // 过滤出近一个月的做题记录
        List<LocalDate> solveDates = allExercise.stream()
                .map(record -> record.getSubmitTime().toLocalDate())
                .distinct()
                .sorted((d1, d2) -> d2.compareTo(d1)) // 降序排序
                .collect(Collectors.toList());

        int continuousDays = 0;
        LocalDate lastDate = now.minusDays(1); // 从昨天开始

        for (LocalDate date : solveDates) {
            if (date.equals(lastDate)) {
                continuousDays++;
                lastDate = lastDate.minusDays(1);
            } else if (date.isBefore(lastDate)) {
                break;
            }
        }

        return continuousDays;
    }

    public int getSolveCount(List<ExerciseRecordPythonDO> allExercise) {
        // 获取所有不同的 exerciseId
        List<Long> distinctExerciseIds = allExercise.stream()
                .map(ExerciseRecordPythonDO::getExerciseId)
                .distinct()
                .collect(Collectors.toList());

        return distinctExerciseIds.size();
    }

    private List<Long> getTop10PopularExerciseIds(List<ExerciseRecordPythonDO> allExercise) {
        // 提取出所有不同的 exercise_id
        Set<Long> exerciseIds = allExercise.stream()
                .map(ExerciseRecordPythonDO::getExerciseId)
                .collect(Collectors.toSet());

        // 存储题目和对应的人数
        Map<Long, Integer> exerciseCountMap = new HashMap<>();

        // 循环遍历 exercise_id，调用 getExerciseRecordsByExerciseID 方法获取每个题目做的人数
        for (Long exerciseId : exerciseIds) {
            int count = exerciseRecordService.getExerciseRecordsByExerciseID(exerciseId);
            exerciseCountMap.put(exerciseId, count);
        }

        // 根据人数对题目进行排序，并取出前 10 个题目
        return exerciseCountMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(10)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

}
