package com.tmcl.siem.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.tmcl.siem.domain.VerificationToken;



public interface VerficationRepo extends CrudRepository<VerificationToken, Long>{

	List<VerificationToken> findVerificationByToken(String token);
	
}
