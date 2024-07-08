package com.stageEte.evaluation.dto;

import com.stageEte.evaluation.model.EvaluationStatus;
import io.swagger.v3.oas.annotations.media.Schema;


public record EvaluationDTO(
        @Schema(description = "ID de developer")
        Long developerId,
        @Schema(description = "ID de manager")
        Long managerId,
        @Schema(description = "Statut de l'eval")
        EvaluationStatus statut
) { }
