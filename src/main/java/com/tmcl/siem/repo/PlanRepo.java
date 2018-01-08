package com.tmcl.siem.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.tmcl.siem.domain.PlanDetails;


public interface PlanRepo extends CrudRepository<PlanDetails, Long>{

	List<PlanDetails> findPlanByPlanName(String planName);
}
