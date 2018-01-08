package com.tmcl.siem.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tmcl.siem.util.SiEMUtil;

@Component
public class SearchguardServiceImpl implements SearchguradService{

	@Value("${elasticsearch.rest.client.url}")
	private String elasticsearchRestUrl;

	private static final String  INTERNAL_ROLES_API = "/_searchguard/api/roles/";


	@Autowired
	private ElasticSearchService elasticsearch;

	@Override
	public String createRole(String tokeName,String indexName) throws ClientProtocolException, IOException {
		HttpClient httpClient = elasticsearch.getHTTPClient();
		String rolename = "sg-"+tokeName+"-role";
		List<String> privileges = SiEMUtil.privileges();

		List<String> clusterRoles =  SiEMUtil.culsterRoles();


		Map<String, Object> rolesMap = Maps.newHashMap();

		rolesMap.put("cluster", clusterRoles);


		Map<String, Object> indciesMap1 = Maps.newHashMap();

		Map<String, Object> indciesMap = Maps.newHashMap();
		indciesMap.put("*", privileges);
		indciesMap1.put(indexName+"-*", indciesMap);

		rolesMap.put("indices", indciesMap1);

		Gson gson = new GsonBuilder().serializeNulls().create();

		StringEntity stringEntity = new StringEntity(gson.toJson(rolesMap));
		stringEntity.setContentType("application/json");
		HttpPut httpPost = 	new HttpPut(elasticsearchRestUrl+INTERNAL_ROLES_API+rolename);
		httpPost.setEntity(stringEntity);
		HttpResponse response = httpClient.execute(httpPost);

		return rolename;
	}

	@Override
	public void createUserInElasticsearch(String userName, String password, String roleName)throws ClientProtocolException, IOException {
		HttpClient httpClient = elasticsearch.getHTTPClient();
		Gson gson = new GsonBuilder().serializeNulls().create();

		Map<String, Object> userMap = Maps.newHashMap();
		if(password!=null){
			userMap.put("password", password);
		}else{
			HttpGet httpGet = 	new HttpGet(elasticsearchRestUrl+"/_searchguard/api/user/"+userName);
			HttpResponse response = httpClient.execute(httpGet);
			String responseContent = EntityUtils.toString(response.getEntity())  ;
			Map<String, Object> dataMap = new GsonBuilder().serializeNulls().create().fromJson(responseContent, Map.class);	

			if(dataMap.get(userName)!=null){
				Map<String, Object> valuesHap = (Map<String, Object>) dataMap.get(userName);
				String hash = (String)valuesHap.get("hash");
				userMap.put("hash", hash);
			}
		}


		userMap.put("roles",Arrays.asList(roleName,"sg_kibana"));

		StringEntity stringEntity = new StringEntity(gson.toJson(userMap));
		stringEntity.setContentType("application/json");
		HttpPut httpPost = 	new HttpPut(elasticsearchRestUrl+"/_searchguard/api/user/"+userName);
		httpPost.setEntity(stringEntity);
		HttpResponse response = httpClient.execute(httpPost);
		String responseContent = EntityUtils.toString(response.getEntity())  ;
		Map<String, Object> dataMap = new GsonBuilder().serializeNulls().create().fromJson(responseContent, Map.class);	

	}

	@Override
	public void mapUsersToRole(String userName, String roleName)throws ClientProtocolException, IOException {
		Map<String, Object> userMap = Maps.newHashMap();
		HttpClient httpClient = elasticsearch.getHTTPClient();
		Gson gson = new GsonBuilder().serializeNulls().create();

		String [] roles= {roleName,"sg_kibana" };

		for(String role:roles) {

			HttpGet httpGet = 	new HttpGet(elasticsearchRestUrl+"/_searchguard/api/rolesmapping/"+role);
			HttpResponse response = httpClient.execute(httpGet);
			String responseContent = EntityUtils.toString(response.getEntity())  ;
			Map<String, Object> dataMap = new GsonBuilder().serializeNulls().create().fromJson(responseContent, Map.class);	
			Map<String, Object> rolesMap = Maps.newHashMap();
			Map<String,Object> usersMap = (Map<String,Object>)dataMap.get(roleName);
			if(usersMap!=null){
				List<String> usersList = (List<String>)usersMap.get("users");

				if(!usersList.contains(userName)){
					usersList.add(userName);
					rolesMap.put("users", usersList);
				}
			}else{
				List<String> newUsersList = Lists.newArrayList();
				newUsersList.add(userName);
				rolesMap.put("users", newUsersList);
			}
			StringEntity stringEntity = new StringEntity(gson.toJson(rolesMap));
			stringEntity.setContentType("application/json");
			HttpPut httpPost = 	new HttpPut(elasticsearchRestUrl+"/_searchguard/api/rolesmapping/"+role);
			httpPost.setEntity(stringEntity);
			HttpResponse postResponse = httpClient.execute(httpPost);
			String reString = EntityUtils.toString(postResponse.getEntity())  ;
			System.out.println(reString);
		}
	}

	




}
