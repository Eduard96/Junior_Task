package com.junior_task.reg_login;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.junior_task.reg_login.models.User;
import com.junior_task.reg_login.repositories.UserRepository;
import com.junior_task.reg_login.security.jwt_config.JwtUtils;
import com.junior_task.reg_login.server_client_interaction.request.LoginRequestInfo;
import com.junior_task.reg_login.server_client_interaction.request.UserDTO;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashSet;
import java.util.Set;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//Testing is not my area

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RegLoginApplicationTests {

    private final LoginRequestInfo validUsernameAndPassword = new LoginRequestInfo();
    private final UserDTO validUserDTOInfo = new UserDTO();
    private final UserDTO wrongUserDTOInfo = new UserDTO();
    private final static String SIGNUP_URI = "/api/auth/signup";
    private final static String SIGNIN_URI = "/api/auth/signin";
    private final User user = new User();
    private final String userName = "Eduard96";
    private final String strongPassword = "qp10QP!ww";

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;


    @BeforeAll
    public void init() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity()).build();
        signUpIfAllInfoIsValid();
    }

    @Test
    public void signInWithValidPasswordAndUserName() throws Exception {
        validUsernameAndPassword.setPassword(strongPassword);
        validUsernameAndPassword.setUserName(userName);
        mockMvc.perform(MockMvcRequestBuilders.post(SIGNIN_URI).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validUsernameAndPassword))).andExpect(status().isOk());
    }

    @Test
    public void signInWithInvalidPassword() throws Exception {
        validUsernameAndPassword.setPassword("invalid password");
        validUsernameAndPassword.setUserName(userName);
        mockMvc.perform(MockMvcRequestBuilders.post(SIGNIN_URI).secure(true).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validUsernameAndPassword))).andExpect(status().isUnauthorized());
    }

    @Test
    public void signInWithInvalidUserName() throws Exception {
        validUsernameAndPassword.setPassword("invalid password");
        validUsernameAndPassword.setUserName(userName);
        mockMvc.perform(MockMvcRequestBuilders.post(SIGNIN_URI).secure(true).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validUsernameAndPassword))).andExpect(status().isUnauthorized());
    }

    @Test
    public void signUpWithWeakPassword() throws Exception {
        Set<String> roles = new HashSet<>();
        roles.add("user");
        wrongUserDTOInfo.setFirstName("Eduard");
        wrongUserDTOInfo.setLastName("Matveev");
        wrongUserDTOInfo.setUserName("Eduard96");
        wrongUserDTOInfo.setPassword("weak password");
        wrongUserDTOInfo.setRoles(roles);
        assert (getResponse(wrongUserDTOInfo).contains("\"errors\""));
    }

    @Test
    public void signUpIfMainInfoTooShort() throws Exception {
        Set<String> roles = new HashSet<>();
        roles.add("user");
        wrongUserDTOInfo.setFirstName("Je");
        wrongUserDTOInfo.setLastName("Ki");
        wrongUserDTOInfo.setUserName("Chan");
        wrongUserDTOInfo.setPassword(strongPassword);
        wrongUserDTOInfo.setRoles(roles);
        assert (getResponse(wrongUserDTOInfo).contains("\"errors\""));
    }

    //@Test
    public void signUpIfAllInfoIsValid() throws Exception {
        Set<String> roles = new HashSet<>();
        roles.add("user");
        validUserDTOInfo.setFirstName("Eduard");
        validUserDTOInfo.setLastName("Matveev");
        validUserDTOInfo.setUserName(userName);
        validUserDTOInfo.setPassword(strongPassword);
        validUserDTOInfo.setRoles(roles);
        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.
                post(SIGNUP_URI).contentType(MediaType.APPLICATION_JSON).
                content(objectMapper.writeValueAsString(validUserDTOInfo))).
                andExpect(status().isOk()).andReturn().getResponse();
        assert  response.getContentAsString().equals("{\"message\":\"User registered successfully!\"}");
    }

    private String getResponse(UserDTO userDTO) throws Exception {
        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.
                post(SIGNUP_URI).contentType(MediaType.APPLICATION_JSON).
                content(objectMapper.writeValueAsString(userDTO))).
                andExpect(status().is4xxClientError()).andReturn().getResponse();
        return response.getContentAsString();
    }

    @Test
    public void mustNotAllowedForUnauthorisedUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/test/user"))
                .andExpect(status().isUnauthorized());
    }

    @AfterAll
    public void clear() {
        if(userRepository.findByUserName(validUserDTOInfo.getUserName()).isPresent()) {
            User user = userRepository.findByUserName(validUserDTOInfo.getUserName()).get();
            userRepository.delete(user);
        }
        if(userRepository.findByUserName(userName).isPresent()) {
            User user = userRepository.findByUserName(userName).get();
            userRepository.delete(user);
        }
    }
}
