package com.junior_task.reg_login;

import com.junior_task.reg_login.models.Role;
import com.junior_task.reg_login.models.RoleEnum;
import com.junior_task.reg_login.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class RegLoginApplication {

    @Autowired
    private RoleRepository roleRepository;

    @PostConstruct
    public void fillInRoleTable() {
        roleRepository.save(new Role(RoleEnum.ROLE_USER));
    }

    public static void main(String[] args) {
        SpringApplication.run(RegLoginApplication.class, args);
    }

}
