package com.echo.AttendenceService.service;

import com.echo.AttendenceService.dto.request.LoginRequest;
import com.echo.AttendenceService.dto.request.RegisterRequest;
import com.echo.AttendenceService.dto.response.AuthResponse;
import com.echo.AttendenceService.entity.RoleEntity;
import com.echo.AttendenceService.entity.UserEntity;
import com.echo.AttendenceService.exception.BadRequestException;
import com.echo.AttendenceService.repository.RoleRepository;
import com.echo.AttendenceService.repository.UserRepository;
import com.echo.AttendenceService.security.JwtUtils;
import com.echo.AttendenceService.security.UserDetailsImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder encoder,
                       JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
    }

    public AuthResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new AuthResponse(jwt, userDetails.getId(), userDetails.getEmail(), userDetails.getName(), roles);
    }

    @Transactional
    public void registerUser(RegisterRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new BadRequestException("Email is already in use!");
        }

        UserEntity user = new UserEntity(
                signUpRequest.getName(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword())
        );

        Set<String> strRoles = signUpRequest.getRoles();
        Set<RoleEntity> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            RoleEntity studentRole = roleRepository.findByName("ROLE_STUDENT")
                    .orElseThrow(() -> new BadRequestException("Role ROLE_STUDENT is not configured"));
            roles.add(studentRole);
        } else {
            strRoles.forEach(role -> {
                RoleEntity foundRole = roleRepository.findByName(role)
                        .orElseThrow(() -> new BadRequestException("Role " + role + " not found"));
                roles.add(foundRole);
            });
        }

        user.setRoles(roles);
        userRepository.save(user);
    }
}