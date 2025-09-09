package com.event.eventservice.web;

import com.event.eventservice.entities.Class;
import com.event.eventservice.entities.Event;
import com.event.eventservice.repositories.ClasseRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

public class ClasseController {
    private ClasseRepository classeRepository;

    public ClasseController(ClasseRepository classeRepository) {
        this.classeRepository = classeRepository;
    }

    @GetMapping("/classes")
    public List<Class> getclasses() {
        System.out.println(classeRepository.findAll());
        return classeRepository.findAll();
    }

    @GetMapping("/classes/{id}")
    public Class getClasse(@PathVariable Long id) {
        return classeRepository.findById(id).get();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/classes/{id}")
    public void deleteClasse(@PathVariable Long id) {
        classeRepository.deleteById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/classes/{id}")
    public void updateClasse(@PathVariable Long id, @RequestBody Class classe) {
        Class classe1 = classeRepository.findById(id).orElseThrow();
        BeanUtils.copyProperties(classe, classe1);
        classeRepository.save(classe1);
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping("/dcrement_classe/{id}")
    public void decrementClasse(@PathVariable Long id) {
        Class classe = classeRepository.findById(id).orElseThrow();
        classe.setCapacity(classe.getCapacity() - 1);
        classeRepository.save(classe);
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping("/increment_classe/{id}")
    public void incrementClasse(@PathVariable Long id) {
        Class classe = classeRepository.findById(id).orElseThrow();
        classe.setCapacity(classe.getCapacity() + 1);
        classeRepository.save(classe);
    }
}
