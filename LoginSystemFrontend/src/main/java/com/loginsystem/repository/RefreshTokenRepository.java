package com.loginsystem.repository;

//repository/RefreshTokenRepository.java

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.loginsystem.entity.RefreshToken;
import com.loginsystem.entity.User;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
	Optional<RefreshToken> findByToken(String token);

	int deleteByUser(User user);
	
//	@Modifying
//	void deleteByUserId(Long userId);
	Optional<RefreshToken> findByUserId(Long userId);
	
}
