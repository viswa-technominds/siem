package com.tmcl.siem.service;


import org.apache.http.impl.client.CloseableHttpClient;
import org.elasticsearch.client.transport.TransportClient;

public interface ElasticSearchService {

	CloseableHttpClient getHTTPClient();
	
	TransportClient getElasticSearchClient();
}
