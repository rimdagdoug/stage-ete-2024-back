package com.stageEte.evaluation.dto;

public class SkillDTO {
    private long skillId;
    private Float note;

    private String name;

    private String description;
    public long getSkillId() {
        return skillId;
    }

    public void setSkillId(long skillId) {
        this.skillId = skillId;
    }

    public Float getNote() {
        return note;
    }

    public void setNote(Float note) {
        this.note = note;
    }
}
