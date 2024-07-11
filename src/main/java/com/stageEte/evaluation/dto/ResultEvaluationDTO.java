package com.stageEte.evaluation.dto;

import io.swagger.v3.oas.annotations.media.Schema;


public record ResultEvaluationDTO(
        @Schema(description = "L'id de compétence",nullable = true)
        Long skillId,
        @Schema(description = "L'id de l'évaluation",nullable = true)
        Long evaluationId,
        @Schema(description = "Commentaire",nullable = true)
        String commentaire,
        @Schema(description = "Note du développeur", nullable = true)
        Float noteDeveloper,
        @Schema(description = "Note du manager",nullable = true)
        Float noteManager,
        @Schema(description = "Note finale",nullable = true)
        Float finalNote
) { }
