package com.springboot.cli.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.springboot.cli.model.DO.python.KnowledgePythonDO;
import com.springboot.cli.model.VO.KnowledgeGraphVO;
import com.springboot.cli.model.VO.exercise.KnowledgeVO;
import com.springboot.cli.model.VO.python.KnowledgeGraphPythonVO;
import com.springboot.cli.model.VO.python.exercise.KnowledgePythonVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface KnowledgePythonMapper extends BaseMapper<KnowledgePythonDO> {

    @Select("with stu_mas as (select * from mastery_python where student_id=#{studentId})" +
            "select k.id, k.parent_id, m.level, m.query, k.name " +
            "from knowledge_python k left join stu_mas m " +
            "on m.knowledge_id=k.id")
    List<KnowledgeGraphPythonVO> getKnowledgeGraphVO(String studentId);

    @Select("SELECT k.id AS knowledge_id, k.knowledge AS knowledge FROM knowledge_python k JOIN exercise_knowledge_python ek ON k.id = ek.knowledge_id WHERE ek.exercise_id = #{exerciseId}")
    List<KnowledgePythonVO> getKnowledgeList(Long exerciseId);
}
