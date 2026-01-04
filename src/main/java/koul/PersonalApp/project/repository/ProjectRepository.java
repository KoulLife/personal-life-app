package koul.PersonalApp.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import koul.PersonalApp.project.Entity.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
