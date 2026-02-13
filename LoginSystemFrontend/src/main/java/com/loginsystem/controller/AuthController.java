package com.loginsystem.controller;

//controller/AuthController.java

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.loginsystem.dto.JwtResponse;
import com.loginsystem.dto.LoginRequest;
import com.loginsystem.dto.MessageResponse;
import com.loginsystem.dto.SignupRequest;
import com.loginsystem.dto.TokenRefreshRequest;
import com.loginsystem.dto.TokenRefreshResponse;
import com.loginsystem.entity.ERole;
import com.loginsystem.entity.RefreshToken;
import com.loginsystem.entity.Role;
import com.loginsystem.entity.User;
import com.loginsystem.exception.TokenRefreshException;
import com.loginsystem.repository.RoleRepository;
import com.loginsystem.repository.UserRepository;
import com.loginsystem.security.JwtUtils;
import com.loginsystem.security.UserDetailsImpl;
import com.loginsystem.services.RefreshTokenService;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	@Autowired
	RefreshTokenService refreshTokenService;

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
		
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
				.collect(Collectors.toList());

		RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

		return ResponseEntity
				.ok(new JwtResponse(jwt, refreshToken.getToken(), "Bearer",userDetails.getId(), userDetails.getEmail(), roles));
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
		}

		// Create new user's account
		User user = new User(signUpRequest.getEmail(), encoder.encode(signUpRequest.getPassword()));

		Set<String> strRoles = signUpRequest.getRoles();
		Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			Role userRole = roleRepository.findByName(ERole.ROLE_USER)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			roles.add(userRole);
		} else {
			strRoles.forEach(role -> {
				switch (role) {
				case "admin":
					Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(adminRole);
					break;
				case "mod":
					Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(modRole);
					break;
				default:
					Role userRole = roleRepository.findByName(ERole.ROLE_USER)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(userRole);
				}
			});
		}

		user.setRoles(roles);
		userRepository.save(user);

		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}

//	@PostMapping("/refreshtoken")
//	public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
//		String requestRefreshToken = request.getRefreshToken();
//
//		return refreshTokenService.findByToken(requestRefreshToken)
//				.map(refreshTokenService::verifyExpiration)
//				.map(RefreshToken::getUser)
//				.map(user -> {
//					String token = jwtUtils.generateTokenFromEmail(user.getEmail());
//					return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
//				})
//				.orElseThrow(() -> new TokenRefreshException(requestRefreshToken, "Refresh token is not in database!"));
//	}
	
	@PostMapping("/refreshtoken")
	public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
	    String requestRefreshToken = request.getRefreshToken();
	    
	    Optional<RefreshToken> tokenOptional = refreshTokenService.findByToken(requestRefreshToken);
	    
	    RefreshToken refreshToken = tokenOptional
	        .orElseThrow(() -> new TokenRefreshException(requestRefreshToken, 
	            "Refresh token is not in database!"));
	    
	    RefreshToken validToken = refreshTokenService.verifyExpiration(refreshToken);
	    User user = validToken.getUser();
	    String token = jwtUtils.generateTokenFromEmail(user.getEmail());
	    
	    return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
	}

	@PostMapping("/signout")
	public ResponseEntity<?> logoutUser() {
		UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		Long userId = userDetails.getId();
		refreshTokenService.deleteByUserId(userId);
		return ResponseEntity.ok(new MessageResponse("Log out successful!"));
	}
}
