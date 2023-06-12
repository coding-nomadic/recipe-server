package com.recipe.server.service;

import com.recipe.server.entity.Role;
import com.recipe.server.entity.User;
import com.recipe.server.exceptions.RecipeServiceException;
import com.recipe.server.exceptions.UserNotFoundException;
import com.recipe.server.models.RegistrationRequest;
import com.recipe.server.repository.RoleRepository;
import com.recipe.server.repository.UserRepository;
import com.recipe.server.utils.EmailUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
@Slf4j
public class RegistrationService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    EmailService emailService;

    @Value("${api.url}")
    String apiUrl;

    public void registerUser(RegistrationRequest registrationRequest) {
        if (userRepository.existsByUserName(registrationRequest.getUserName())) {
            throw new RecipeServiceException("Username already exists!", "104");
        }
        if (userRepository.existsByEmail(registrationRequest.getEmail())) {
            throw new RecipeServiceException("Email already exists!", "104");
        }
        String tokenForNewUser = UUID.randomUUID().toString();
        User user = user(registrationRequest, tokenForNewUser);
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName("ROLE_USER").get();
        roles.add(userRole);
        user.setRoles(roles);
        userRepository.save(user);
        String link = apiUrl + "/signup/confirm?token=" + tokenForNewUser;
        emailService.sendMail(registrationRequest.getEmail(), EmailUtils.buildEmail(registrationRequest.getFullName(), link));
    }

    /**
     * @param registrationRequest
     * @return
     */
    private User user(RegistrationRequest registrationRequest, String token) {
        User user = new User();
        user.setUserName(registrationRequest.getUserName());
        user.setFullName(registrationRequest.getFullName());
        user.setEmail(registrationRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
        user.setMobileNumber(registrationRequest.getMobileNumber());
        user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
        user.setToken(token);
        user.setEnabled(false);
        return user;
    }

    public String confirmToken(String token) {
        if (userRepository.existsByToken(token)) {
            log.info("Token exists in the user DB, hence updating the isEnabled parameter");
        } else {
            throw new UserNotFoundException("Token not found for the user");
        }
        User user = userRepository.findByToken(token).orElseThrow(() -> new UserNotFoundException("Not found"));
        user.setEnabled(true);
        userRepository.save(user);
        log.info("isEnabled Parameter is updated in DB");
        return "Your email is confirmed. Thank you for using our service!";
    }
}
