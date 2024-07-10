package com.stageEte.evaluation.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ResultEvaluation {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "Skills_id",nullable = false)
    private  Skills skills;

    @ManyToOne
    @JoinColumn(name = "evaluation_id",nullable = false)
    private Evaluation evaluation;

    private String commentaire;

    private Float noteDeveloper;
    private Float noteManager;
    private Float finalNote;

    @Enumerated(EnumType.STRING)
    private EvaluationStatus status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private Date createdAt = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;

    @PrePersist
    protected void onCreate() {
        updatedAt = createdAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }

}
