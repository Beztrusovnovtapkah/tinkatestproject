package ru.alitryel.bfmetvennorath.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.alitryel.bfmetvennorath.entities.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}