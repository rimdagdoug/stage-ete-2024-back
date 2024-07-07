package com.stageEte.evaluation.service;

import com.stageEte.evaluation.model.Skills;
import com.stageEte.evaluation.repository.SkillsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SkillsService {

    private final SkillsRepository skillsRepository;
    public ResponseEntity<List<Skills>> listSkills() {
        try{
            List<Skills> skills = new ArrayList<>();
            skillsRepository.findAll().forEach(skills::add);

            if(skills.isEmpty()){
                return new ResponseEntity<>(skills, HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(skills, HttpStatus.OK);

        }catch (Exception ex){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Skills> detailSkill(Integer id) {
        Optional<Skills> skill = skillsRepository.findById(id);
        if(skill.isPresent()){
            return  new ResponseEntity<>(skill.get(),HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<Skills> addSkill(Skills skill) {
        return new ResponseEntity<>(skillsRepository.save(skill), HttpStatus.OK);
    }

    public ResponseEntity<Skills> updateSkill(Integer id, Skills request) {
        try {
            Optional<Skills> skillToUpdate = skillsRepository.findById(id);
            if (skillToUpdate.isPresent()) {
                Skills skill = skillToUpdate.get();
                skill.setName(request.getName());
                skill.setDescription(request.getDescription());
                skill.setCoefficient(request.getCoefficient());
                skill.setUpdatedAt(new Date()); // Update the timestamp
                return new ResponseEntity<>(skillsRepository.save(skill), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> deleteSkill(Integer id) {
        try {
            if (skillsRepository.existsById(id)) {
                skillsRepository.deleteById(id);
                return new ResponseEntity<>("Skill deleted successfully", HttpStatus.OK);
            } else {

                return new ResponseEntity<>("Skill not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception ex) {
            return new ResponseEntity<>("An error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}