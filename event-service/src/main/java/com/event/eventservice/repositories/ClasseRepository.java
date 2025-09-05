package com.event.eventservice.repositories;

import com.event.eventservice.entities.Class;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClasseRepository extends JpaRepository<Class, Long> {
}
