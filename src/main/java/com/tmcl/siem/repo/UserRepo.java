package com.tmcl.siem.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tmcl.siem.domain.UserDetails;

@Repository
public interface UserRepo extends CrudRepository<UserDetails, Long> {

	List<UserDetails> findByUserName(String userName);
	
	List<UserDetails> findByAccessToken(String token);
	
	List<UserDetails> findUserByCompanyName(String companyName);
	
}
