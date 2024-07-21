package com.stageEte.evaluation.repository;

import com.stageEte.evaluation.model.Role;
import com.stageEte.evaluation.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);

    @Query("SELECT u FROM User u WHERE u.role = :role")
    List<User> findAllByRole(Role role);

    default List<User> findAllManagers() {
        return findAllByRole(Role.MANAGER);
    }

    default List<User> findAllDevelopers() {
        return findAllByRole(Role.DEVELOPER);
    }


}
