package com.tmcl.siem.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.client.ClientProtocolException;

public interface SearchguradService {

	
	String createRole(String tokeName,String indexName) throws ClientProtocolException, IOException;
	
	void createUserInElasticsearch(String userName,String password,String roleName)throws ClientProtocolException, IOException;
	
	void mapUsersToRole(String userName,String roleName)throws ClientProtocolException, IOException;

	
	
	
}
