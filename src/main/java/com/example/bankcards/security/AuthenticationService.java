package com.example.bankcards.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.bankcards.dto.JwtAuthenticationResponse;
import com.example.bankcards.dto.SignInRequest;
import com.example.bankcards.dto.SignUpRequest;
import com.example.bankcards.entity.ROLE;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.DAO.UserServiceDAO;
import lombok.RequiredArgsConstructor;




@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final JwtService jwtService;
    private final UserServiceDAO userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;



    @Value("${app.admin.phone}")
    private String adminPhone ;

    
    public JwtAuthenticationResponse signUp(SignUpRequest request ) {
        var user = User.builder()
                .username(generateUsernameFromPhone(request.getPhoneNumber()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getPhoneNumber().equals(adminPhone) ? ROLE.ROLE_ADMIN : ROLE.ROLE_USER)
                .build();
  
        userService.create(user);
        var jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponse(jwt);
    }


    public JwtAuthenticationResponse signIn(SignInRequest request) {
        try {
            var user = userService.userDetailsService()
                .loadUserByUsername(userService.getUsernameByPhoneNumber(request.getPhoneNumber()));
                
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    user.getUsername(), 
                    request.getPassword()
                )
            );
       
            

            var jwt = jwtService.generateToken(user);
            return new JwtAuthenticationResponse(jwt);

        } catch (Exception e) {
            throw new RuntimeException("Invalid phone number or password");
        }
    }


    private String generateUsernameFromPhone(String phoneNumber) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(phoneNumber.getBytes(StandardCharsets.UTF_8));
            return "user_" + HexFormat.of().formatHex(hash).substring(0, 12);
        } catch (NoSuchAlgorithmException e) {
            throw new AuthenticationServiceException("Username generation failed");
        }
    }
}