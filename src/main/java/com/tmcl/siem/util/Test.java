package com.tmcl.siem.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import com.floragunn.searchguard.ssl.SearchGuardSSLPlugin;
import com.google.common.collect.Lists;
import com.google.gson.GsonBuilder;

public class Test {

	public static void main(String[] args) throws ClientProtocolException, IOException {
		
		HttpClient httpClient = getHTTPClient();
		
		HttpGet httpGet = 	new HttpGet("https://ec2-35-165-7-68.us-west-2.compute.amazonaws.com:9200/_searchguard/api/rolesmapping/sg-tjtbfclcrpfnidfrdukf-role");
		HttpResponse response = httpClient.execute(httpGet);
		String responseContent = EntityUtils.toString(response.getEntity())  ;
		System.out.println(responseContent);
		
		/*Map<String, Object> dataMap = new GsonBuilder().serializeNulls().create().fromJson(responseContent, Map.class);	
		List<String> roles = Lists.newArrayList();
		for(Map.Entry<String, Object> rolesMap:dataMap.entrySet()){
			if(rolesMap.getKey().indexOf("sg-")!=-1) {
				HttpDelete httpDelete = 	new HttpDelete("https://ec2-35-165-7-68.us-west-2.compute.amazonaws.com:9200/_searchguard/api/roles/"+rolesMap.getKey());
				HttpResponse response1 = httpClient.execute(httpDelete);
				String responseContent1 = EntityUtils.toString(response1.getEntity())  ;
				System.out.println(responseContent1);
			}
		}*/
		
		//System.out.println(roles);
	}
	
	public  static  CloseableHttpClient getHTTPClient()  {
		try{
			final HttpClientBuilder hcb = HttpClients.custom();

			final KeyStore myTrustStore = KeyStore.getInstance("JKS");
			myTrustStore.load(new FileInputStream("/Users/prasanthviswanadham/Documents/softwares/technominds_search_gurad/search-guard-certificates/truststore.jks"),
					"32820796144581f5b960".toCharArray());

			final KeyStore keyStore = KeyStore.getInstance("JKS");
			keyStore.load(new FileInputStream("/Users/prasanthviswanadham/Documents/softwares/technominds_search_gurad/search-guard-certificates/client-certificates/CN=sgadmin-keystore.jks"), "624fa9ede9ca8d3ea59a".toCharArray());

			final SSLContextBuilder sslContextbBuilder = SSLContexts.custom().useProtocol("TLS");


			sslContextbBuilder.loadTrustMaterial(new File("/Users/prasanthviswanadham/Documents/softwares/technominds_search_gurad/search-guard-certificates/truststore.jks"), "32820796144581f5b960".toCharArray());



			sslContextbBuilder.loadKeyMaterial(keyStore, "624fa9ede9ca8d3ea59a".toCharArray());


			final SSLContext sslContext = sslContextbBuilder.build();

			String[] protocols = null;


			protocols = new String[] { "TLSv1", "TLSv1.1", "TLSv1.2" };


			final SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, protocols, null,
					SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			hcb.setSSLSocketFactory(sslsf);


			hcb.setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(60 * 1000).build());

			return hcb.build();
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
