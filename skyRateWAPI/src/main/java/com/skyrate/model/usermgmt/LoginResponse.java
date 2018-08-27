package com.skyrate.model.usermgmt;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skyrate.clib.model.BaseResponse;
import com.skyrate.model.dbentity.User;

public class LoginResponse extends BaseResponse{
	
	User userProfile;
	long roleId;
	String userActive;
	String userRefresh;
	
	public User getUserProfile() {
		return userProfile;
	}

	public void setUserProfile(User userProfile) {
		this.userProfile = userProfile;
	}
	
	public long getRoleId() {
		return roleId;
	}

	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}

	public String getUserActive() {
		return userActive;
	}

	public void setUserActive(String userActive) {
		this.userActive = userActive;
	}

	public String getUserRefresh() {
		return userRefresh;
	}

	public void setUserRefresh(String userRefresh) {
		this.userRefresh = userRefresh;
	}
	

	
}
