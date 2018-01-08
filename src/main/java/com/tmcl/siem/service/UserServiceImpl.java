package com.tmcl.siem.service;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tmcl.siem.domain.ElasticsearchStorge;
import com.tmcl.siem.domain.PlanDetails;
import com.tmcl.siem.domain.UserDetails;
import com.tmcl.siem.domain.VerificationToken;
import com.tmcl.siem.exception.PasswordMathcerExpection;
import com.tmcl.siem.model.UserModel;
import com.tmcl.siem.repo.PlanRepo;
import com.tmcl.siem.repo.UserRepo;
import com.tmcl.siem.repo.VerficationRepo;
import com.tmcl.siem.util.RoleEnum;
import com.tmcl.siem.util.SiEMUtil;
import com.tmcl.siem.util.SiemConstants;

@Component
@Service
public class UserServiceImpl implements UserService{

	//%{+YYYY.MM.dd}

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private VerficationRepo verficationRepo;

	@Autowired
	private ElasticSearchService elasticSearch;

	@Autowired
	private SearchguradService searchguardService;

	@Autowired
	private PlanRepo planRepo;


	@Override
	public UserDetails saveUserDetails(UserModel userModel) {

		UserDetails userDetails= new UserDetails();
		BeanUtils.copyProperties(userModel, userDetails);
		userDetails.setCreatedDate(new Date());
		userDetails.setStatus("created");
		userDetails.setPassword(passwordEncoder.encode(userModel.getPassword()));
		userDetails.setElasticsearchPassword(SiEMUtil.encryptString(userModel.getPassword()));
		userDetails.setRoleName(RoleEnum.ROLE_COMPANY_ADMIN.toString());
		return userRepo.save(userDetails);
	}

	@Override
	public UserDetails getUserDetailsBasedOnToken(String token) {
		List<UserDetails> userDetails = userRepo.findByAccessToken(token);
		if(userDetails.isEmpty()){
			throw new UsernameNotFoundException("Invaid Token");
		}

		return userDetails.get(0);


	}


	@Override
	public void createVerificationTokenForUser(UserDetails user, String token) {

		final VerificationToken verificationToken = new VerificationToken(token, user);

		verficationRepo.save(verificationToken);
	}

	@Override
	public String validateEmail(String token) {

		List<VerificationToken> verificationToken = verficationRepo.findVerificationByToken(token);
		if(verificationToken.isEmpty()) {
			return SiemConstants.TOKEN_INVALID;
		}

		final UserDetails user = verificationToken.get(0).getUser();

		final Calendar cal = Calendar.getInstance();
		if ((verificationToken.get(0).getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
			verficationRepo.delete(verificationToken);
			return SiemConstants.TOKEN_EXPIRED;

		}

		List<PlanDetails> planDetails = planRepo.findPlanByPlanName("SiEM Basic");

		String userToken = SiEMUtil.generateRandomToken() ;
		user.setAccessToken(userToken);
		user.setPlanDetails(planDetails.get(0));
		user.setStatus("Active");


		String indexName = SiEMUtil.bulidIndexName(userToken);

		ElasticsearchStorge elasticsearchStorge = new ElasticsearchStorge();
		elasticsearchStorge.setElasticsearchIndexName(indexName);
		elasticsearchStorge.setIndexRetentionPeriod("1d");
		elasticsearchStorge.setUserDetails(user);
		user.setElasticsearchStorge(elasticsearchStorge);

		createElasticSearchIndex(indexName);
		generateWelcomeMessage(indexName);

		String roleName = createCompanyRole(userToken, indexName);
		createUserInElasticSearch(user.getUserName(), user.getElasticsearchPassword(), roleName);
		mapUserToRoles(user.getUserName(),roleName);
		createIndexInKibana(indexName);
		userRepo.save(user);
		return SiemConstants.TOKEN_VALID;

	}



	@Override
	public List<UserDetails> findByUserName(String userName) {

		return userRepo.findByUserName(userName);
	}

	@Override
	public boolean createElasticSearchIndex(String indexname) {
		CreateIndexResponse  response = elasticSearch.getElasticSearchClient().admin().indices().prepareCreate(indexname+"-"+sdf.format(new Date())).get();
		return response.isAcknowledged();
	}

	private void generateWelcomeMessage(String indexName) {

		Map<String, Object> dataMap = Maps.newHashMap();
		dataMap.put("@timestamp", new Date());

		dataMap.put("message", "Weclome to SiEM");
		//%{+YYYY.MM.dd}
		elasticSearch.getElasticSearchClient().prepareIndex(indexName+"-"+sdf.format(new Date()), "miscellaneous", UUID.randomUUID().toString())
		.setSource(dataMap)
		.get();

	}

	private String createCompanyRole(String tokenName,String indexName) {
		try {
			return searchguardService.createRole(tokenName, indexName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return StringUtils.EMPTY;

	}


	private void createUserInElasticSearch(String userName,String password, String roleName) {
		try {
			searchguardService.createUserInElasticsearch(userName, password, roleName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void createIndexInKibana(String indexName) {

		Gson gson = new GsonBuilder().serializeNulls().create();

		Map<String, Object> dataMap = Maps.newHashMap();
		dataMap.put("timeFieldName", "@timestamp");
		dataMap.put("title",  indexName+"-*");
		dataMap.put("fields", gson.toJson(SiEMUtil.getFieldsForKibanaIndex()));

		elasticSearch.getElasticSearchClient().prepareIndex(".kibana", "index-pattern", indexName+"-*")
		.setSource(dataMap)
		.get();

		elasticSearch.getElasticSearchClient().admin().indices().prepareRefresh(".kibana").get();



	}

	private void mapUserToRoles(String userName,String roleName) {
		try {
			searchguardService.mapUsersToRole(userName, roleName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void changePassword(UserModel userModel) throws PasswordMathcerExpection {
		List<UserDetails> userDetails =  userRepo.findByUserName(userModel.getUserName());
		if(userDetails.isEmpty()) {
			throw new UsernameNotFoundException("User was not found");
		}
		if(!passwordEncoder.matches(userModel.getPassword(), userDetails.get(0).getPassword())) {
			throw new PasswordMathcerExpection("Current password wasn't mathc");
		}
		UserDetails userDetails2 = userDetails.get(0);
		userDetails2.setPassword(passwordEncoder.encode(userModel.getNewPassword()));
		userRepo.save(userDetails2);

	}
	@Override
	public void changePasswordAfterLogin(UserModel userModel) throws PasswordMathcerExpection{
		List<UserDetails> userDetails =  userRepo.findByUserName(userModel.getUserName());
		if(userDetails.isEmpty()) {
			throw new UsernameNotFoundException("User was not found");
		}
		if(!passwordEncoder.matches(userModel.getPassword(), userDetails.get(0).getPassword())) {
			throw new PasswordMathcerExpection("Current password wasn't mathc");
		}
		UserDetails userDetails2 = userDetails.get(0);
		userDetails2.setPassword(passwordEncoder.encode(userModel.getNewPassword()));
		userDetails2.setRoleName(RoleEnum.ROLE_COMPANY_USER.toString());
		userDetails2.setStatus("Active");

		String indexName = userDetails2.getElasticsearchStorge().getElasticsearchIndexName();




		String roleName = createCompanyRole(userDetails2.getAccessToken(), indexName);
		createUserInElasticSearch(userDetails2.getUserName(), userDetails2.getElasticsearchPassword(), roleName);
		mapUserToRoles(userDetails2.getUserName(),roleName);


		userRepo.save(userDetails2);

	}




	@Override
	public List<Map<String, Object>> findUsersByCompany(String companyName) {
		List<UserDetails> userDetails = userRepo.findUserByCompanyName(companyName);
		List<Map<String, Object>> dataMap =Lists.newArrayList();
		for(UserDetails users:userDetails) {
			Map<String, Object>  usersMap = Maps.newHashMap();
			usersMap.put("userName", users.getUserName());
			usersMap.put("fullName", users.getFullName());
			usersMap.put("status", users.getStatus());
			dataMap.add(usersMap);

		}

		return dataMap;
	}

	@Override
	public UserDetails createLocalUsers(UserModel userModel) {



		List<UserDetails> existingUserDetails = userRepo.findByAccessToken(userModel.getAccessToken());
		if(existingUserDetails.isEmpty()) {
			throw new UsernameNotFoundException("Token Not Found");
		}

		UserDetails userDetails = new UserDetails();
		userDetails.setAccessToken(userModel.getAccessToken());
		userDetails.setCreatedDate(new Date());
		userDetails.setFullName(userModel.getFullName());
		userDetails.setUserName(userModel.getUserName());
		userDetails.setStatus("Password Reset");
		userDetails.setPassword(passwordEncoder.encode(userModel.getPassword()));
		userDetails.setCompanyName(userModel.getCompany());
		userDetails.setPlanDetails(existingUserDetails.get(0).getPlanDetails());

		userDetails.setElasticsearchPassword(SiEMUtil.encryptString(userModel.getPassword()));

		String indexName = existingUserDetails.get(0).getElasticsearchStorge().getElasticsearchIndexName();

		ElasticsearchStorge elasticsearchStorge = new ElasticsearchStorge();
		elasticsearchStorge.setElasticsearchIndexName(indexName);
		elasticsearchStorge.setIndexRetentionPeriod("1d");
		elasticsearchStorge.setUserDetails(userDetails);
		userDetails.setElasticsearchStorge(elasticsearchStorge);


		return userRepo.save(userDetails);
	}





}
