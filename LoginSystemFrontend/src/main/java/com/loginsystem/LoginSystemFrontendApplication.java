package com.loginsystem;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.loginsystem.entity.ERole;
import com.loginsystem.entity.Role;
import com.loginsystem.repository.RoleRepository;

@SpringBootApplication
public class LoginSystemFrontendApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoginSystemFrontendApplication.class, args);
	}

	@Bean
	CommandLineRunner initDatabase(RoleRepository roleRepository) {
		return args -> {
			// Initialize roles if they don't exist
			if (roleRepository.count() == 0) {
				roleRepository.save(new Role(ERole.ROLE_USER));
				roleRepository.save(new Role(ERole.ROLE_MODERATOR));
				roleRepository.save(new Role(ERole.ROLE_ADMIN));
				System.out.println("Roles initialized!");
			}
		};
	}

}
