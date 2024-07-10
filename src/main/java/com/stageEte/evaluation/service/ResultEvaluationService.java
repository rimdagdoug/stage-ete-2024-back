package com.stageEte.evaluation.service;

import com.stageEte.evaluation.dto.ResultEvaluationDTO;
import com.stageEte.evaluation.model.ResultEvaluation;
import com.stageEte.evaluation.model.Role;
import com.stageEte.evaluation.model.User;
import com.stageEte.evaluation.repository.EvaluationRepository;
import com.stageEte.evaluation.repository.ResultEvaluationRepository;
import com.stageEte.evaluation.repository.SkillsRepository;
import com.stageEte.evaluation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import com.stageEte.evaluation.model.EvaluationStatus;

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

    public ResponseEntity<ResultEvaluation> addResultEvaluation(ResultEvaluationDTO dto) {
        try {
            ResultEvaluation resultEvaluation = new ResultEvaluation();

            // Trouver l'évaluation par son ID
            var evaluation = evaluationRepository.findById(dto.evaluationId())
                    .orElseThrow(() -> new RuntimeException("Evaluation not found"));

            // Trouver la compétence par son ID
            var skill = skillsRepository.findById(dto.skillId())
                    .orElseThrow(() -> new RuntimeException("Skill not found"));

            resultEvaluation.setEvaluation(evaluation);
            resultEvaluation.setSkills(skill);

            // Sauvegarder le résultat de l'évaluation
            resultEvaluationRepository.save(resultEvaluation);

            return new ResponseEntity<>(resultEvaluation, HttpStatus.CREATED);

        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ResultEvaluation> noteInput(Long id, ResultEvaluationDTO dto, Principal connectedUser) {
        try {
            var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

            Optional<ResultEvaluation> resultOptional = resultEvaluationRepository.findById(id);
                if (resultOptional.isEmpty()) {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
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
            else{
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            ResultEvaluation savedResult = resultEvaluationRepository.save(result);
            return new ResponseEntity<>(savedResult, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

