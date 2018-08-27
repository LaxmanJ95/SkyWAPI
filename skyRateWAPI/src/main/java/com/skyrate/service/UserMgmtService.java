package com.skyrate.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skyrate.clib.util.JSONUtil;
import com.skyrate.dao.UserActivityRepository;
import com.skyrate.dao.UserQuery;
import com.skyrate.dao.UserRepository;
import com.skyrate.dao.UserRoleMappingRepository;
import com.skyrate.model.dbentity.User;
import com.skyrate.model.dbentity.UserActivity;
import com.skyrate.model.dbentity.UserRoleMapping;
import com.skyrate.model.report.UserCount;
import com.skyrate.model.usermgmt.AddProfileRequest;
import com.skyrate.model.usermgmt.UpdateUserProfileRequest;
import com.skyrate.model.usermgmt.UserCountResponse;

@Service
@Transactional
public class UserMgmtService {
	@Autowired
	private  UserRepository userRepository;
	
	@Autowired
	private  UserQuery userQuery;	
	
	@Autowired
	UserActivityRepository userActivityRepository;
	
	@Autowired
	UserRoleMappingRepository userRoleMappingRepository;
	
	public UserMgmtService( ) {
			//this.userRepository = userRepository;
	}
	public User saveUser(User user){
		User user2 = this.userRepository.save(user);
		return user2;
	}
	public User login(String email, String password){
		return this.userQuery.loginUser(email, password);
	}
	
	public User userNameExist(String userName){
		return this.userRepository.findByUserName(userName);
	}	
	public long getUserRoleId(long userId){
		return userQuery.getUserRoleId(userId);
	}
	public User getPasswordByUserName(String userName){
		return this.userRepository.findByUserName(userName);
	}

	public User getUserByEmail(String email){

		return this.userRepository.findByEmail(email);
	}
	public User changePassword(int id,String password){
		return this.userRepository.findByIdAndPassword(id,password);
	}
	public User getUserById(int id){
	  return this.userRepository.findById(id);
	}
	public User emailIdExist(String email){
		return this.userRepository.findByEmail(email);
	}
	public UserActivity saveAvtivity(UserActivity userActivity){
		return this.userActivityRepository.save(userActivity);
	}
	public UserActivity getActivityByToken(String value){
		return this.userActivityRepository.findBySecureToken(value);
	}
	public UserActivity getActivityByRefreshToken(String refreshToken){
		return this.userActivityRepository.findByUserRefreshToken(refreshToken);
	}
	public UserCount getUserCount(String date){
		return this.userQuery.getUserCount(date);
	}
	
	public UserRoleMapping getByRoleId(int roleId){
		return this.userRoleMappingRepository.findByRoleId(roleId);
	}
	
	public User getUser(int id){
		return this.userQuery.getUserById(id);
	}
}



