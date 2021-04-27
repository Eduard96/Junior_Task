package com.junior_task.reg_login.services;

import com.junior_task.reg_login.models.Role;
import com.junior_task.reg_login.models.RoleEnum;
import com.junior_task.reg_login.models.User;
import com.junior_task.reg_login.repositories.RoleRepository;
import com.junior_task.reg_login.repositories.UserRepository;
import com.junior_task.reg_login.server_client_interaction.request.UserDTO;
import com.junior_task.reg_login.server_client_interaction.response.MessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {

    private static final String ROLE_ERROR_MSG = "Error: Role is not found";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public ResponseEntity<?> save(UserDTO userDTO) {
        User user = new User(userDTO.getFirstName(), userDTO.getLastName(),
                            userDTO.getUserName(), passwordEncoder.encode(userDTO.getPassword()));

        user.setRoles(getRoles(userDTO.getRoles()));
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }



    public boolean checkExistsByUserName(String userName) {
        return userRepository.existsByUserName(userName);
    }

    public Set<Role> getRoles(Set<String> rolesFromRequest) {
        Set<Role> roles = new HashSet<>();

        if(rolesFromRequest.isEmpty()) throw new RuntimeException(ROLE_ERROR_MSG);

        rolesFromRequest.forEach(role -> {
            switch (role.toLowerCase()) {
                case "user":
                    Role userRole = roleRepository.findByName(RoleEnum.ROLE_USER).
                            orElseThrow(() -> new RuntimeException(ROLE_ERROR_MSG));
                    roles.add(userRole);
                    break;
                case "for future roles":
                    //...
                    break;
                default:
                    throw new RuntimeException(ROLE_ERROR_MSG);
            }
        });
        return roles;
    }
}
