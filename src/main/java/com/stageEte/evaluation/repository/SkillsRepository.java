package com.stageEte.evaluation.repository;

import com.stageEte.evaluation.model.Role;
import com.stageEte.evaluation.model.Skills;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillsRepository extends JpaRepository<Skills,Long> {
    List<Skills> findByskillType(Role skillType);


}
