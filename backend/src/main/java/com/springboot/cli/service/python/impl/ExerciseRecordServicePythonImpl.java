package com.springboot.cli.service.python.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.springboot.cli.common.enums.OpExceptionEnum;
import com.springboot.cli.common.exception.OpException;
import com.springboot.cli.model.DO.python.ExerciseRecordPythonDO;
import com.springboot.cli.model.VO.exercise.ExerciseRecordPage;
import com.springboot.cli.model.VO.python.exercise.ExerciseRecordPythonPage;
import com.springboot.cli.repository.impl.ExerciseRecordPythonRepository;
import com.springboot.cli.service.ExerciseRecordService;
import com.springboot.cli.service.python.ExerciseRecordPythonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExerciseRecordServicePythonImpl implements ExerciseRecordPythonService {
    @Autowired
    private ExerciseRecordPythonRepository exerciseRecordPythonRepository;

    @Override
    public Integer hasDoneExercise(String studentId, Long exerciseId) {
        if(studentId == null || exerciseId == null) throw new OpException(OpExceptionEnum.ILLEGAL_ARGUMENT);
        LambdaQueryWrapper<ExerciseRecordPythonDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExerciseRecordPythonDO::getStudentId, studentId).eq(ExerciseRecordPythonDO::getExerciseId, exerciseId);
        List<ExerciseRecordPythonDO> exerciseRecordList = exerciseRecordPythonRepository.list(queryWrapper);
        return (exerciseRecordList == null || exerciseRecordList.isEmpty()) ? 0 : 1;
    }

    @Override
    public List<ExerciseRecordPythonDO> getExerciseRecord(String studentId, Long questionId) {
        LambdaQueryWrapper<ExerciseRecordPythonDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExerciseRecordPythonDO::getExerciseId, questionId);
        queryWrapper.eq(ExerciseRecordPythonDO::getStudentId, studentId);
        return exerciseRecordPythonRepository.list(queryWrapper);
    }

    @Override
    public ExerciseRecordPythonDO getExerciseRecord(Long recordId) {
        return exerciseRecordPythonRepository.getById(recordId);
    }

    @Override
    public List<ExerciseRecordPythonDO> getStudentAllExerciseRecord(String studentId) {
        LambdaQueryWrapper<ExerciseRecordPythonDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExerciseRecordPythonDO::getStudentId, studentId);
        return exerciseRecordPythonRepository.list(queryWrapper);
    }

    @Override
    public int getExerciseRecordsByExerciseID(Long exerciseID) {
        LambdaQueryWrapper<ExerciseRecordPythonDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExerciseRecordPythonDO::getExerciseId, exerciseID);
        List<ExerciseRecordPythonDO> records = exerciseRecordPythonRepository.list(queryWrapper);
        List<String> distinctStudentIds = records.stream()
                .map(ExerciseRecordPythonDO::getStudentId)
                .distinct()
                .collect(Collectors.toList());
        return distinctStudentIds.size();
    }

    @Override
    public ExerciseRecordPythonPage page(Integer pageSize, Integer pageNum, String studentId) {
        Page<ExerciseRecordPythonDO> page = new Page<>(pageNum, pageSize);
        page.addOrder(new OrderItem("submit_time", false));
        LambdaQueryWrapper<ExerciseRecordPythonDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExerciseRecordPythonDO::getStudentId, studentId);
        page = exerciseRecordPythonRepository.page(page, queryWrapper);
        return new ExerciseRecordPythonPage(page.getRecords(), page.getTotal(), page.getPages());
    }
}
