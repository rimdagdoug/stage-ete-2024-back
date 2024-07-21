package com.stageEte.evaluation.controller;

import com.stageEte.evaluation.model.Skills;
import com.stageEte.evaluation.service.SkillsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class SkillsController {

    private final SkillsService service;


    @PreAuthorize("hasAnyRole('RH')")
    @GetMapping("/skills")
    public ResponseEntity<List<Skills>> listSkills(){
        return service.listSkills();
    }

    @GetMapping("/skills/{id}")
    public ResponseEntity<Skills> detailSkill(@PathVariable Long id){
        return service.detailSkill(id);
    }

    @PostMapping("/skills")
    public ResponseEntity<Skills> addSkill(@RequestBody Skills skill){
        return service.addSkill(skill);
    }

    @PutMapping("/skills/{id}")
    public ResponseEntity<Skills> updateSkill(@PathVariable Long id, @RequestBody Skills request){
        return  service.updateSkill(id,request);
    }

    @DeleteMapping("/skills/{id}")
    public ResponseEntity<String> deleteSkill(@PathVariable Long id) {
        return service.deleteSkill(id);
    }


}
