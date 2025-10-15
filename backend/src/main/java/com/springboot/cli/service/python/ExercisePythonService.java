package com.springboot.cli.service.python;

import com.springboot.cli.model.DO.python.ExercisePythonDO;
import com.springboot.cli.model.VO.exercise.ExercisePage;
import com.springboot.cli.model.VO.exercise.FeedBackVO;
import com.springboot.cli.model.VO.python.exercise.ExercisePythonPage;
import com.springboot.cli.model.VO.python.exercise.FeedBackPythonVO;

import java.util.List;

public interface ExercisePythonService {
    ExercisePythonDO getExerciseById(Long id);

    FeedBackPythonVO getFeedBack(Long id, String answer, Integer submitNum);

    FeedBackPythonVO getSelectFeedBack(Long id, String choice);

    Integer getExerciseLNumber(int type);

    List<ExercisePythonDO> getRecList(String studentId, Integer questionNum);

    ExercisePythonPage page(Integer type, Integer pageNum, Integer pageSize, Integer difficulty, List<Long> knowledgeId, Integer difficultyOrder, String chapter);
}
