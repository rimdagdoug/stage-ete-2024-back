package com.stageEte.evaluation.dto;

import com.stageEte.evaluation.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;


public record ResultEvaluationMultipleDTO(
        @Schema(description = "La liste  des ids de compétence",nullable = true)
        List<Long> skillId,
        @Schema(description = "L'id de l'évaluation",nullable = true)
        Long evaluationId,
        @Schema(description = "Le type de rôle", nullable = true)
        Role role
) { }
