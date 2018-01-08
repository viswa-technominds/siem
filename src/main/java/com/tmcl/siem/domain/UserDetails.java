package com.tmcl.siem.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Table(name="user_details")
@Entity
public class UserDetails implements Serializable {

	
	
	@Id
	@Column(name="id")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	@Column(name="username")
	private String userName;
	
	@Column(name="company_name")
	private String companyName;
	
	

	@Column(name="password")
	private String password;
	
	@Column(name="created_date")
	private Date createdDate;
	
	@Column(name="status")
	private String status;
	
	@Column(name="full_name")
	private String fullName;
	
	@Column(name="phone_number")
	private String phoneNumber;
	
	@Column(name="role_name")
	private String roleName;
	
	@Column(name="access_type")
	private String accessType;

	@Column(name="access_token")
	private String accessToken;
	
	@Column(name="els_pass")
	private String elasticsearchPassword;
	
	@OneToOne(fetch = FetchType.LAZY,
            cascade =  CascadeType.ALL,
            mappedBy = "userDetails")	
	private ElasticsearchStorge elasticsearchStorge;
	

	@ManyToOne(cascade = CascadeType.ALL)
	private PlanDetails planDetails;
	

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	
	
	public PlanDetails getPlanDetails() {
		return planDetails;
	}

	public void setPlanDetails(PlanDetails planDetails) {
		this.planDetails = planDetails;
	}

	public String getElasticsearchPassword() {
		return elasticsearchPassword;
	}

	public void setElasticsearchPassword(String elasticsearchPassword) {
		this.elasticsearchPassword = elasticsearchPassword;
	}

	public ElasticsearchStorge getElasticsearchStorge() {
		return elasticsearchStorge;
	}

	public void setElasticsearchStorge(ElasticsearchStorge elasticsearchStorge) {
		this.elasticsearchStorge = elasticsearchStorge;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getAccessType() {
		return accessType;
	}

	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	
	
}
