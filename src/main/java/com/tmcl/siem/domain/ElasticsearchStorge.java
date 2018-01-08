package com.tmcl.siem.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="user_elasticsearch_settings")
public class ElasticsearchStorge implements Serializable {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id")
	private long id;
	
	@Column(name="elasticsearch_indexname")
	private String elasticsearchIndexName;
	
	@Column(name="index_retention_period")
	private String indexRetentionPeriod;
	
	
	
	
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="user_id")
	private UserDetails userDetails;

	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getElasticsearchIndexName() {
		return elasticsearchIndexName;
	}

	public void setElasticsearchIndexName(String elasticsearchIndexName) {
		this.elasticsearchIndexName = elasticsearchIndexName;
	}

	public String getIndexRetentionPeriod() {
		return indexRetentionPeriod;
	}

	public void setIndexRetentionPeriod(String indexRetentionPeriod) {
		this.indexRetentionPeriod = indexRetentionPeriod;
	}

	public UserDetails getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(UserDetails userDetails) {
		this.userDetails = userDetails;
	}
	
	
	
}
