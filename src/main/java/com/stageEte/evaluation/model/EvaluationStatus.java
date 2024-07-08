package com.stageEte.evaluation.model;

public enum EvaluationStatus {
    // Statuts pour le développeur
    AWAITING_DEVELOPER_INPUT,
    DEVELOPER_INPUT_COMPLETED,

    // Statuts pour le manager
    AWAITING_MANAGER_VALIDATION,
    VALIDATED_BY_MANAGER,


    // Statuts pour le RH
    AWAITING_HR_APPROVAL,
    COMPLETED,
    ABANDONED

}
