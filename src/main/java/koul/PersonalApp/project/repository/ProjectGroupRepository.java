package koul.PersonalApp.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import koul.PersonalApp.project.Entity.ProjectGroup;

public interface ProjectGroupRepository extends JpaRepository<ProjectGroup, Long> {
}
