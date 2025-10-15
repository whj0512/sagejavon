package com.springboot.cli.service.python.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.springboot.cli.common.enums.OpExceptionEnum;
import com.springboot.cli.common.exception.OpException;
import com.springboot.cli.common.jwt.AuthStorage;
import com.springboot.cli.model.DO.ExerciseDO;
import com.springboot.cli.model.DO.ExerciseKnowledgeDO;
import com.springboot.cli.model.DO.ExerciseRecordDO;
import com.springboot.cli.model.DO.python.ExerciseKnowledgePythonDO;
import com.springboot.cli.model.DO.python.ExercisePythonDO;
import com.springboot.cli.model.DO.python.ExerciseRecordPythonDO;
import com.springboot.cli.model.VO.exercise.ExercisePage;
import com.springboot.cli.model.VO.exercise.FeedBackVO;
import com.springboot.cli.model.VO.python.exercise.ExercisePythonPage;
import com.springboot.cli.model.VO.python.exercise.FeedBackPythonVO;
import com.springboot.cli.repository.impl.ExerciseKnowledgeRepository;
import com.springboot.cli.repository.impl.ExerciseRecordRepository;
import com.springboot.cli.repository.impl.ExerciseRepository;
import com.springboot.cli.repository.impl.ExerciseKnowledgePythonRepository;
import com.springboot.cli.repository.impl.ExercisePythonRepository;
import com.springboot.cli.repository.impl.ExerciseRecordPythonRepository;
import com.springboot.cli.service.ExerciseService;
import com.springboot.cli.service.python.ExercisePythonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.springboot.cli.common.CommonConstants.PYTHON_SERVICE;
import static com.springboot.cli.common.CommonConstants.submitNumThreshold;

@Service
public class ExerciseServicePythonImpl implements ExercisePythonService {
    @Autowired
    ExercisePythonRepository exerciseRepository;

    @Autowired
    ExerciseRecordPythonRepository exerciseRecordRepository;

    @Autowired
    ExerciseKnowledgePythonRepository exerciseKnowledgeRepository;

    @Autowired
    RestTemplate restTemplate;

    @Override
    public ExercisePythonDO getExerciseById(Long id) {
        return exerciseRepository.getById(id);
    }

    @Override
    public FeedBackPythonVO getFeedBack(Long id, String answer, Integer submitNum) {
        ExercisePythonDO exercise = exerciseRepository.getById(id);
        if (exercise == null) throw new OpException(OpExceptionEnum.ILLEGAL_ARGUMENT);
        String url = PYTHON_SERVICE + "/get_code_score";
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("question", exercise.getQuestionText());
        requestBody.put("code", answer);
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(requestBody, requestHeaders);
        String result;
        try{
            result = restTemplate.postForObject(url, httpEntity, String.class);
        } catch (Exception e) {
            throw new OpException(OpExceptionEnum.LLM_ERROR);
        }
        if (result == null) throw new OpException(OpExceptionEnum.LLM_ERROR);
        JSONObject json = JSONObject.parseObject(result);
        String data = json.getString("data");
        JSONObject dataJson = JSONObject.parseObject(data);
        String suggestion = dataJson.getString("suggestion");
        Integer codingStyle = dataJson.getInteger("codingStyle");
        Integer functionalCorrectness = dataJson.getInteger("functionalCorrectness");
        Integer usefulness = dataJson.getInteger("usefulness");
        double score = Math.round((codingStyle + functionalCorrectness + usefulness) / 12.0 * 100.0) / 100.0;
        ExerciseRecordPythonDO exerciseRecord = ExerciseRecordPythonDO.builder()
                .exerciseId(id)
                .studentId(AuthStorage.getUser().getUserId())
                .answer(answer)
                .score(score)
                .suggestion(suggestion)
                .type(0)
                .submitTime(LocalDateTime.now())
                .build();
        exerciseRecordRepository.save(exerciseRecord);
        String correctAnswer = null;
        if (submitNum >= submitNumThreshold)
            correctAnswer = exercise.getCorrectAnswer();
        return FeedBackPythonVO.builder()
                .correctAnswer(correctAnswer)
                .score((int)(score * 100))
                .suggestion(suggestion)
                .build();
    }

    @Override
    public FeedBackPythonVO getSelectFeedBack(Long id, String choice) {
        ExercisePythonDO exercise = exerciseRepository.getById(id);
        if (exercise == null) throw new OpException(OpExceptionEnum.ILLEGAL_ARGUMENT);
        String correctAnswer = exercise.getCorrectAnswer();
        if (choice.equals(correctAnswer)) {
            ExerciseRecordPythonDO exerciseRecord = ExerciseRecordPythonDO.builder()
                    .exerciseId(id)
                    .studentId(AuthStorage.getUser().getUserId())
                    .answer(choice)
                    .score(1.0)
                    .type(1)
                    .submitTime(LocalDateTime.now())
                    .build();
            exerciseRecordRepository.save(exerciseRecord);
            return FeedBackPythonVO.builder()
                    .score(1)
                    .build();
        } else {
            ExerciseRecordPythonDO exerciseRecord = ExerciseRecordPythonDO.builder()
                    .exerciseId(id)
                    .studentId(AuthStorage.getUser().getUserId())
                    .answer(choice)
                    .score(0.0)
                    .type(1)
                    .submitTime(LocalDateTime.now())
                    .build();
            exerciseRecordRepository.save(exerciseRecord);
            return FeedBackPythonVO.builder()
                    .score(0)
                    .build();
        }
    }

    @Override
    public Integer getExerciseLNumber(int type) {
        return exerciseRepository.getExerciseNumber(type);
    }

    @Override
    public List<ExercisePythonDO> getRecList(String studentId, Integer questionNum) {
        String url = PYTHON_SERVICE + "/get_recommend_list?userId={studentId}&questionNum={questionNum}";
        String result;
        try{
            result = restTemplate.getForEntity(url, String.class, studentId, questionNum).getBody();
        } catch (Exception e) {
            throw new OpException(OpExceptionEnum.REC_ERROR);
        }
        if (result == null) throw new OpException(OpExceptionEnum.REC_ERROR);
        JSONObject json = JSONObject.parseObject(result);
        String data = json.getString("data");
        JSONObject dataJson = JSONObject.parseObject(data);
        JSONArray jsonArray = dataJson.getJSONArray("qList");
        List<Integer> recQuestionList = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            recQuestionList.add(jsonArray.getInteger(i));
        }
        LambdaQueryWrapper<ExercisePythonDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ExercisePythonDO::getId, recQuestionList);
        return exerciseRepository.list(queryWrapper);
    }

    @Override
    public ExercisePythonPage page(Integer type, Integer pageNum, Integer pageSize, Integer difficulty, List<Long> knowledgeId, Integer difficultyOrder, String chapter) {
        Page<ExercisePythonDO> page = new Page<>(pageNum, pageSize);
        page.addOrder(new OrderItem("id", true));
        LambdaQueryWrapper<ExercisePythonDO> queryWrapper = new LambdaQueryWrapper<>();
        // 筛选题目类型
        if (type != null) {
            queryWrapper.eq(ExercisePythonDO::getType, type);
        }
        if (difficulty != null)
            queryWrapper.eq(ExercisePythonDO::getDifficulty, difficulty);
        if (knowledgeId != null && !knowledgeId.isEmpty()) {
            LambdaQueryWrapper<ExerciseKnowledgePythonDO> exerciseKnowledgeDOLambdaQueryWrapper = new LambdaQueryWrapper<>();
            exerciseKnowledgeDOLambdaQueryWrapper.in(ExerciseKnowledgePythonDO::getKnowledgeId, knowledgeId);
            List<ExerciseKnowledgePythonDO> exerciseKnowledgeList = exerciseKnowledgeRepository.list(exerciseKnowledgeDOLambdaQueryWrapper);
            List<Long> exerciseId = new ArrayList<>();
            exerciseKnowledgeList.forEach(exerciseKnowledge -> exerciseId.add(exerciseKnowledge.getExerciseId()));
            queryWrapper.in(ExercisePythonDO::getId, exerciseId);
        }
        if (chapter != null) {
            queryWrapper.eq(ExercisePythonDO::getChapter, chapter);
        }
        if (difficultyOrder != 0)
            queryWrapper.orderBy(true, difficultyOrder == 1, ExercisePythonDO::getDifficulty);
        page = exerciseRepository.page(page, queryWrapper);
        return new ExercisePythonPage(page.getRecords(), page.getTotal(), page.getPages());
    }
}
