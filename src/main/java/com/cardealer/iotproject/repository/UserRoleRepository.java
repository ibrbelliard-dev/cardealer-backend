package com.cardealer.iotproject.repository;

import com.cardealer.iotproject.model.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    Optional<UserRole> findByRoleTitle(String roleTitle);
    boolean existsByRoleTitle(String roleTitle);
}