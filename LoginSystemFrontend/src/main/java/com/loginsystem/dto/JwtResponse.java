package com.loginsystem.dto;

//dto/JwtResponse.java

//import lombok.AllArgsConstructor;
//import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

//@Data
@NoArgsConstructor
//@AllArgsConstructor
public class JwtResponse {
	private String accessToken;
	private String refreshToken;
	private String type = "Bearer";
	private Long id;
	private String email;
	private List<String> roles;
	public JwtResponse(String accessToken, String refreshToken, String type, Long id, String email,
			List<String> roles) {
		super();
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.type = type;
		this.id = id;
		this.email = email;
		this.roles = roles;
	}
	// Explicit getters
    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public String getType() { return type; }
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public List<String> getRoles() { return roles; }
	
}
