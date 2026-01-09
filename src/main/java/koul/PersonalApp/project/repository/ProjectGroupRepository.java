package koul.PersonalApp.project.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import koul.PersonalApp.project.Entity.ProjectGroup;

public interface ProjectGroupRepository extends JpaRepository<ProjectGroup, Long> {
    Optional<ProjectGroup> findByProjectGroupIdAndUser_UserId(Long projectGroupId, Long userId);

    java.util.List<ProjectGroup> findAllByUser_UserId(Long userId);
}
