package com.springboot.cli.service.python;

public interface ReviewPythonService {
    Integer review(Long exerciseId, Integer review);

    Integer getReview(String studentId, Long exerciseId);
}
