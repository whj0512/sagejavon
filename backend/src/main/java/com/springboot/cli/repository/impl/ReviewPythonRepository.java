package com.springboot.cli.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.cli.mapper.ReviewPythonMapper;
import com.springboot.cli.model.DO.python.ReviewPythonDO;
import com.springboot.cli.repository.IReviewPythonRepo;
import org.springframework.stereotype.Service;

@Service
public class ReviewPythonRepository extends ServiceImpl<ReviewPythonMapper, ReviewPythonDO> implements IReviewPythonRepo {
}
