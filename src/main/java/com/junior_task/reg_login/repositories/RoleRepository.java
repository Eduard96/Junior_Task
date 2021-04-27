package com.junior_task.reg_login.repositories;

import com.junior_task.reg_login.models.Role;
import com.junior_task.reg_login.models.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleEnum name);

}
