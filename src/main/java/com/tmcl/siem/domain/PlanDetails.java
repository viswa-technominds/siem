package com.tmcl.siem.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="plan_details")
@Entity
public class PlanDetails implements Serializable {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="plan_id")
	private long id;
	
	@Column(name="plan_name")
	private String planName;
	
	@Column(name="daily_ingest")
	private long dailyIngest;
	
	@Column(name="retention")
	private long retention;
	
	@Column(name="max_dashboards_alerts")
	private String maxDashboards;
	
	@Column(name="max_number_of_users")
	private String maxNumberOfUsers;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPlanName() {
		return planName;
	}

	public void setPlanName(String planName) {
		this.planName = planName;
	}

	public long getDailyIngest() {
		return dailyIngest;
	}

	public void setDailyIngest(long dailyIngest) {
		this.dailyIngest = dailyIngest;
	}

	public long getRetention() {
		return retention;
	}

	public void setRetention(long retention) {
		this.retention = retention;
	}

	public String getMaxDashboards() {
		return maxDashboards;
	}

	public void setMaxDashboards(String maxDashboards) {
		this.maxDashboards = maxDashboards;
	}

	public String getMaxNumberOfUsers() {
		return maxNumberOfUsers;
	}

	public void setMaxNumberOfUsers(String maxNumberOfUsers) {
		this.maxNumberOfUsers = maxNumberOfUsers;
	}
	
	
	
}
