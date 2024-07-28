package com.stageEte.evaluation.dto;

import java.util.List;

public class NoteEvalDTO {
    private List<SkillDTO> skills;
    private long evaluationId;


    public List<SkillDTO> getSkills() {
        return skills;
    }

    public void setSkills(List<SkillDTO> skills) {
        this.skills = skills;
    }

    public long getEvaluationId() {
        return evaluationId;
    }

    public void setEvaluationId(long evaluationId) {
        this.evaluationId = evaluationId;
    }
}