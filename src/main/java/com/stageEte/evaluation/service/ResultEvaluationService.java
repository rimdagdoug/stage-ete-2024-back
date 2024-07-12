package com.stageEte.evaluation.service;

import com.stageEte.evaluation.dto.NoteEvalDTO;
import com.stageEte.evaluation.dto.ResultEvaluationDTO;
import com.stageEte.evaluation.dto.ResultEvaluationMultipleDTO;
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

    public ResponseEntity<ResultEvaluation> detailEvaluation(Long id){
        Optional<ResultEvaluation> resultEvaluation = resultEvaluationRepository.findById(id);
        return resultEvaluation.map(value -> new ResponseEntity<>(value,HttpStatus.OK))
                .orElseGet(()->new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    public ResponseEntity<String> addMultipleResultEvaluations(ResultEvaluationMultipleDTO dto) {
        try {

            for (Long skillId : dto.skillId()) {
                createNewResultEvaluation(dto.evaluationId(), skillId);
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

        // Trouver la compétence par son ID
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
            var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

            for (SkillDTO eval : dto.getSkills()) {
                ResultEvaluation resultOptional = resultEvaluationRepository.findByEvaluationIdAndSkillsId(dto.getEvaluationId(), eval.getSkillId());
                if (resultOptional == null) {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
                inputNoteSkill(eval.getNote(), user, resultOptional);
            }

            return new ResponseEntity<>("saved Result", HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResultEvaluation inputNoteSkill(Float note, User user, ResultEvaluation result) {
        if (note != null && user.getRole() == Role.DEVELOPER && user.getId() == result.getEvaluation().getDeveloper().getId()) {
            result.setNoteDeveloper(note);
            result.setStatus(EvaluationStatus.DEVELOPER_INPUT_COMPLETED);
        }

        else if (note != null && user.getRole() == Role.MANAGER && user.getId() == result.getEvaluation().getManager().getId()) {
            result.setNoteManager(note);
            result.setStatus(EvaluationStatus.VALIDATED_BY_MANAGER);
        }

        else if (note != null && user.getRole() == Role.RH) {
            result.setFinalNote(note);
            result.setStatus(EvaluationStatus.COMPLETED);
        }
        ResultEvaluation savedResult = resultEvaluationRepository.save(result);
        return savedResult;
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

