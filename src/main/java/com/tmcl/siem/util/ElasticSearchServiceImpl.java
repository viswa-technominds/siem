package com.tmcl.siem.util;

import java.io.File;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.util.Collection;

import javax.net.ssl.SSLContext;

import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.floragunn.searchguard.ssl.SearchGuardSSLPlugin;
import com.tmcl.siem.service.ElasticSearchService;

@Configuration
@PropertySource("classpath:/application.properties")
@EnableAutoConfiguration
public class ElasticSearchServiceImpl implements ElasticSearchService {

	@Value("${elasticsearch.hostname}")
	private String elasticsearchHostName;
	
	@Value("${elasticsearch.searchgurad.jks.path}")
	private String searchGuradJKSPath;
	
	@Value("${elasticsearch.searchgurad.admin.path}")
	private String searchGuradAdminPath;
	
	@Value("${elasticsearch.searchguard.truststore.password}")
	private String trustStorePassword;
	
	@Value("${elasticsearch.searchgurard.admin.password}")
	private String adminPassword;
	
	
	
	
	
	
	
	
	public   CloseableHttpClient getHTTPClient()  {
		try{
			final HttpClientBuilder hcb = HttpClients.custom();

			final KeyStore myTrustStore = KeyStore.getInstance("JKS");
			myTrustStore.load(new FileInputStream(searchGuradJKSPath),
					trustStorePassword.toCharArray());

			final KeyStore keyStore = KeyStore.getInstance("JKS");
			keyStore.load(new FileInputStream(searchGuradAdminPath), adminPassword.toCharArray());

			final SSLContextBuilder sslContextbBuilder = SSLContexts.custom().useProtocol("TLS");


			sslContextbBuilder.loadTrustMaterial(new File(searchGuradJKSPath), trustStorePassword.toCharArray());



			sslContextbBuilder.loadKeyMaterial(keyStore, adminPassword.toCharArray());


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
	
	/*Settings settings = Settings.builder()
			.put("path.home", ".")
			.put("cluster.name", "searchguard_demo")
			.put("searchguard.ssl.transport.enabled", true)
			.put("searchguard.ssl.transport.keystore_filepath", "/Users/prasanthviswanadham/Documents/softwares/technominds_search_gurad/search-guard-certificates/client-certificates/CN=sgadmin-keystore.jks")
			.put("searchguard.ssl.transport.truststore_filepath", "/Users/prasanthviswanadham/Documents/softwares/technominds_search_gurad/search-guard-certificates/truststore.jks")
			.put("searchguard.ssl.transport.keystore_password", "624fa9ede9ca8d3ea59a")  
			.put("searchguard.ssl.transport.truststore_password", "32820796144581f5b960")  

			.build();
*/

	@Override
	public TransportClient getElasticSearchClient() {
		Settings settings = Settings.builder()
				.put("path.home", ".")
				.put("cluster.name", "TECHHOMINDS")
				.put("searchguard.ssl.transport.enabled", true)
				.put("searchguard.ssl.transport.keystore_filepath", "/Users/prasanthviswanadham/Documents/softwares/technominds_search_gurad/search-guard-certificates/client-certificates/CN=sgadmin-keystore.jks")
				.put("searchguard.ssl.transport.truststore_filepath", "/Users/prasanthviswanadham/Documents/softwares/technominds_search_gurad/search-guard-certificates/truststore.jks")
				.put("searchguard.ssl.transport.keystore_password", "624fa9ede9ca8d3ea59a")  
				.put("searchguard.ssl.transport.truststore_password", "32820796144581f5b960")  

				.build();


		Collection plugins = com.google.common.collect.Lists.newArrayList();
		plugins.add(SearchGuardSSLPlugin.class);
		
		 try {
			return new PreBuiltTransportClient(settings,plugins)
					.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("ec2-35-165-7-68.us-west-2.compute.amazonaws.com"), 9300));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return null;
	}
	
	
}
