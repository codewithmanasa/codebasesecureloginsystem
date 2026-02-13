package com.loginsystem.services;

//service/RefreshTokenService.java

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.loginsystem.entity.RefreshToken;
import com.loginsystem.exception.TokenRefreshException;
import com.loginsystem.repository.RefreshTokenRepository;
import com.loginsystem.repository.UserRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

	@Value("${jwt.refresh.expiration}")
	private Long refreshTokenDurationMs;

	@Autowired
	private RefreshTokenRepository refreshTokenRepository;

	@Autowired
	private UserRepository userRepository;

	public Optional<RefreshToken> findByToken(String token) {
		return refreshTokenRepository.findByToken(token);
	}

	public RefreshToken createRefreshToken(Long userId) {
		
		// Delete existing refresh token for this user first
	    //refreshTokenRepository.deleteByUserId(userId);
		
		RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
		        .orElse(new RefreshToken());
	    
		//RefreshToken refreshToken = new RefreshToken();

		refreshToken.setUser(userRepository.findById(userId).get());
		refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
		refreshToken.setToken(UUID.randomUUID().toString());

		refreshToken = refreshTokenRepository.save(refreshToken);
		return refreshToken;
	}

	public RefreshToken verifyExpiration(RefreshToken token) {
		if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
			refreshTokenRepository.delete(token);
			throw new TokenRefreshException(token.getToken(),
					"Refresh token was expired. Please make a new signin request");
		}

		return token;
	}

	@Transactional
	public int deleteByUserId(Long userId) {
		return refreshTokenRepository.deleteByUser(userRepository.findById(userId).get());
	}
}
