package com.loginsystem.security;

//security/UserDetailsServiceImpl.java

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.loginsystem.entity.User;
import com.loginsystem.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
		
		System.out.println("Found user: " + user.getEmail());
		System.out.println("Password from entity: " + user.getPassword());
		
		
		UserDetails userDetails = UserDetailsImpl.build(user);
	    System.out.println("Password in UserDetails: " + userDetails.getPassword());
	    
	    return userDetails;
	    
		//return UserDetailsImpl.build(user);
	}
}
