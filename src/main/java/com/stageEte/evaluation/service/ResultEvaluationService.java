package com.stageEte.evaluation.service;

import com.stageEte.evaluation.dto.NoteEvalDTO;
import com.stageEte.evaluation.dto.ResultEvaluationDTO;
import com.stageEte.evaluation.dto.SkillDTO;
import com.stageEte.evaluation.model.*;
import com.stageEte.evaluation.repository.EvaluationRepository;
import com.stageEte.evaluation.repository.ResultEvaluationRepository;
import com.stageEte.evaluation.repository.SkillsRepository;
import com.stageEte.evaluation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResultEvaluationService {
    private final SkillsRepository skillsRepository;
    private final ResultEvaluationRepository resultEvaluationRepository;
    private final EvaluationRepository evaluationRepository;
    private final UserRepository userRepository;
    public ResponseEntity<List<ResultEvaluation>> listResultEvaluations(){
        try {
            List<ResultEvaluation> resultEvaluations = resultEvaluationRepository.findAll();
            if(resultEvaluations.isEmpty()){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(resultEvaluations,HttpStatus.OK);
        }catch (Exception ex){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<List<ResultEvaluation>> getResultatEvaluationByIdEval(Long idEval){
        List<ResultEvaluation> resultEvaluation = resultEvaluationRepository.findByEvaluationId(idEval);
        return new ResponseEntity<>(resultEvaluation, HttpStatus.OK);
    }

    public ResponseEntity<String> addSkillsToEvaluation(Role role, Long idEvaluation) {
        try {
        List<Skills> skills = skillsRepository.findByskillType(role);
        for (Skills skill : skills) {
            createNewResultEvaluation(idEvaluation, skill.getId());
        }
        return new ResponseEntity<>("Success insert", HttpStatus.CREATED);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResultEvaluation createNewResultEvaluation(Long evaluationId, Long skillId) {
        ResultEvaluation resultEvaluation = new ResultEvaluation();

        // Trouver l'évaluation par son ID
        var evaluation = evaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new RuntimeException("Evaluation not found"));

        // Trouver la compétence par son ID (remarque : ici, l'ID est déjà connu)
        var skill = skillsRepository.findById(skillId)
                .orElseThrow(() -> new RuntimeException("Skill not found"));

        resultEvaluation.setEvaluation(evaluation);
        resultEvaluation.setSkills(skill);

        // Sauvegarder le résultat de l'évaluation
        resultEvaluationRepository.save(resultEvaluation);
        return resultEvaluation;
    }

    public ResponseEntity<String> noteInputMultiple(NoteEvalDTO dto, Principal connectedUser) {
        try {
            var user = (org.springframework.security.core.userdetails.User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

            for (SkillDTO eval : dto.getSkills()) {
                ResultEvaluation resultOptional = resultEvaluationRepository.findByEvaluationIdAndSkillsId(dto.getEvaluationId(), eval.getSkillId());
                if (resultOptional == null) {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
                inputNoteSkill(eval.getNote(), user, resultOptional);
            }
            String jsonResponse = "{\"message\": \"saved Result\"}";
            return ResponseEntity.ok(jsonResponse);

        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResultEvaluation inputNoteSkill(Float note, org.springframework.security.core.userdetails.User user, ResultEvaluation result) {
        Evaluation evaluation = result.getEvaluation();

        // Check if the developer is inputting their note
        if (note != null && isHasAuthority(user, Role.DEVELOPER.name())) {
            result.setNoteDeveloper(note);
            result.setStatus(EvaluationStatus.AWAITING_MANAGER_VALIDATION);
            evaluation.setStatus(EvaluationStatus.AWAITING_MANAGER_VALIDATION);
        }
        // Check if the manager is inputting their note and the status is not AWAITING_DEVELOPER_INPUT
        else if (note != null && isHasAuthority(user, Role.MANAGER.name()) && !result.getStatus().equals(EvaluationStatus.AWAITING_DEVELOPER_INPUT)) {
            result.setNoteManager(note);
            result.setStatus(EvaluationStatus.VALIDATED_BY_MANAGER);
            evaluation.setStatus(EvaluationStatus.VALIDATED_BY_MANAGER);
        }
        // Check if the RH is inputting the final note and the status is AWAITING_HR_APPROVAL
        else if (note != null && isHasAuthority(user, Role.RH.name()) && !result.getStatus().equals(EvaluationStatus.AWAITING_HR_APPROVAL)) {
            result.setFinalNote(note);
            result.setStatus(EvaluationStatus.COMPLETED);
            evaluation.setStatus(EvaluationStatus.COMPLETED);
        }
        // If the manager tries to input a note while the status is AWAITING_DEVELOPER_INPUT, throw an exception
        else if (note != null && isHasAuthority(user, Role.MANAGER.name()) && result.getStatus().equals(EvaluationStatus.AWAITING_DEVELOPER_INPUT)) {
            throw new IllegalStateException("Le manager ne peut pas saisir les notes tant que le statut est AWAITING_DEVELOPER_INPUT.");
        }
        // If the RH tries to input a note while the status is not AWAITING_HR_APPROVAL, throw an exception
        else if (note != null && isHasAuthority(user, Role.RH.name()) && !result.getStatus().equals(EvaluationStatus.AWAITING_HR_APPROVAL)) {
            throw new IllegalStateException("Le RH ne peut saisir les notes que lorsque le statut est AWAITING_HR_APPROVAL.");
        }

        // Check if both developer and manager have input their notes
        if (result.getNoteDeveloper() != null && result.getNoteManager() != null) {
            evaluation.setStatus(EvaluationStatus.AWAITING_HR_APPROVAL);
        }

        // Save the updated evaluation and result
        evaluationRepository.save(evaluation);
        ResultEvaluation savedResult = resultEvaluationRepository.save(result);
        return savedResult;
    }



    private static boolean isHasAuthority(org.springframework.security.core.userdetails.User user , String role) {
        return user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals(role));
    }

    private ResultEvaluation inputNote(ResultEvaluationDTO dto, User user, Optional<ResultEvaluation> resultOptional) {
        ResultEvaluation result = resultOptional.get();


        if (dto.noteDeveloper() != null && user.getRole() == Role.DEVELOPER && user.getId() == result.getEvaluation().getDeveloper().getId()) {
            result.setNoteDeveloper(dto.noteDeveloper());
            result.setStatus(EvaluationStatus.DEVELOPER_INPUT_COMPLETED);
        }

       else if (dto.noteManager() != null && user.getRole() == Role.MANAGER && user.getId() == result.getEvaluation().getManager().getId()) {
            result.setNoteManager(dto.noteManager());
            result.setStatus(EvaluationStatus.VALIDATED_BY_MANAGER);
        }

       else if (dto.finalNote() != null && user.getRole() == Role.RH) {
            result.setFinalNote(dto.finalNote());
            result.setStatus(EvaluationStatus.COMPLETED);
        }
        ResultEvaluation savedResult = resultEvaluationRepository.save(result);
        return savedResult;
    }


    public ResponseEntity<List<ResultEvaluation>> getAllNoteResultEvaluations() {
        try {
            List<ResultEvaluation> resultEvaluations = resultEvaluationRepository.findAll();

            if (resultEvaluations.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(resultEvaluations, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public ResponseEntity<Float> updateFinalScore(Long evaluationId) {
        try {
            List<ResultEvaluation> resultEvaluations = resultEvaluationRepository.findByEvaluationId(evaluationId);
            Evaluation eval = evaluationRepository.findById(evaluationId).get();
            if (resultEvaluations.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            float sumNotes = 0;
            float sumCoefficients = 0;

            for (ResultEvaluation result : resultEvaluations) {
                if (result.getFinalNote() == null || result.getStatus() != EvaluationStatus.COMPLETED) {
                    throw new Exception("Evaluations est toujours en cours de saisir");
                }else{
                    Float finalNote = result.getFinalNote();
                    Float coefficient = result.getSkills().getCoefficient();
                    if (finalNote != null && coefficient != null) {
                        sumNotes += finalNote * coefficient;
                        sumCoefficients += coefficient;
                    }
                }
            }
            float finalNote = sumNotes / sumCoefficients;
            // here we update eval table
            eval.setStatus(EvaluationStatus.COMPLETED);
            eval.setFinalNote(finalNote);
            evaluationRepository.save(eval);
            return new ResponseEntity<>(finalNote, HttpStatus.OK);

        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}

