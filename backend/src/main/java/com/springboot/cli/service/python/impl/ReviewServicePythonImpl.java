package com.springboot.cli.service.python.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.springboot.cli.common.enums.OpExceptionEnum;
import com.springboot.cli.common.exception.OpException;
import com.springboot.cli.common.jwt.AuthStorage;
import com.springboot.cli.model.DO.ReviewDO;
import com.springboot.cli.model.DO.python.ReviewPythonDO;
import com.springboot.cli.repository.impl.ReviewRepository;
import com.springboot.cli.repository.impl.ReviewPythonRepository;
import com.springboot.cli.service.ReviewService;
import com.springboot.cli.service.python.ReviewPythonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ReviewServicePythonImpl implements ReviewPythonService {
    @Autowired
    private ReviewPythonRepository reviewRepository;

    @Override
    public Integer review(Long exerciseId, Integer review) {
        if (exerciseId == null || review == null) throw new OpException(OpExceptionEnum.ILLEGAL_ARGUMENT);
        String studentId = AuthStorage.getUser().getUserId();
        LambdaQueryWrapper<ReviewPythonDO> reviewDOLambdaQueryWrapper = new LambdaQueryWrapper<>();
        reviewDOLambdaQueryWrapper.eq(ReviewPythonDO::getExerciseId, exerciseId);
        reviewDOLambdaQueryWrapper.eq(ReviewPythonDO::getStudentId, studentId);
        ReviewPythonDO reviewDO = reviewRepository.getOne(reviewDOLambdaQueryWrapper);
        if (reviewDO == null) {
            reviewDO = ReviewPythonDO.builder()
                    .studentId(studentId)
                    .exerciseId(exerciseId)
                    .review(review)
                    .build();
            reviewRepository.save(reviewDO);
        }
        else {
            reviewDO.setReview(review);
            reviewRepository.updateById(reviewDO);
        }
        return review;
    }

    @Override
    public Integer getReview(String studentId, Long exerciseId) {
        if (exerciseId == null || studentId == null) throw new OpException(OpExceptionEnum.ILLEGAL_ARGUMENT);
        LambdaQueryWrapper<ReviewPythonDO> reviewDOLambdaQueryWrapper = new LambdaQueryWrapper<>();
        reviewDOLambdaQueryWrapper.eq(ReviewPythonDO::getExerciseId, exerciseId);
        reviewDOLambdaQueryWrapper.eq(ReviewPythonDO::getStudentId, studentId);
        ReviewPythonDO reviewDO = reviewRepository.getOne(reviewDOLambdaQueryWrapper);
        return reviewDO == null ? 0 : reviewDO.getReview();
    }
}
