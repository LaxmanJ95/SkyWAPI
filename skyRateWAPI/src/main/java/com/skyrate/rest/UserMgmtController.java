package com.skyrate.rest;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import org.slf4j.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.skyrate.clib.model.SuccessIDResponse;
import com.skyrate.clib.service.email.EmailService;
import com.skyrate.clib.service.email.EmailTemplateService;
import com.skyrate.clib.util.DateTimeUtil;
import com.skyrate.clib.util.JSONUtil;
import com.skyrate.clib.util.SecureData;
import com.skyrate.model.dbentity.User;
import com.skyrate.model.dbentity.UserActivity;
import com.skyrate.model.email.EmailMessage;
import com.skyrate.model.report.UserCount;
import com.skyrate.model.usermgmt.AddressIDResponse;
import com.skyrate.model.usermgmt.ChangePasswordRequest;
import com.skyrate.model.usermgmt.ChangePasswordResponse;
import com.skyrate.model.usermgmt.CookieRequest;
import com.skyrate.model.usermgmt.EmailIdExistRequest;
import com.skyrate.model.usermgmt.EmailIdExistResponse;
import com.skyrate.model.usermgmt.ForgotPasswordRequest;
import com.skyrate.model.usermgmt.ForgotPasswordResponse;
import com.skyrate.model.usermgmt.ForgotUsernameRequest;
import com.skyrate.model.usermgmt.ForgotUsernameResponse;
import com.skyrate.model.usermgmt.LoginRequest;
import com.skyrate.model.usermgmt.LoginResponse;
import com.skyrate.model.usermgmt.UpdateUserProfileRequest;
import com.skyrate.model.usermgmt.UpdateUserProfileResponse;
import com.skyrate.model.usermgmt.UserByIdRequest;
import com.skyrate.model.usermgmt.UserCountRequest;
import com.skyrate.model.usermgmt.UserCountResponse;
import com.skyrate.model.usermgmt.UserDetailResponse;
import com.skyrate.model.usermgmt.UserNameExistRequest;
import com.skyrate.model.usermgmt.UserNameExistResponse;
import com.skyrate.model.usermgmt.UserProfile;
import com.skyrate.model.usermgmt.UserSignupRequest;
import com.skyrate.model.usermgmt.UserSignupResponse;
import com.skyrate.service.UserMgmtService;

@RestController
@RequestMapping("/usermgmt")
@CrossOrigin( maxAge = 3600)
public class UserMgmtController {
	private static final Logger logger =  LoggerFactory.getLogger(UserMgmtController.class);
	@Autowired
	private UserMgmtService userMgmtService;
	@Autowired
	private EmailService emailService;
	@Autowired
	private EmailTemplateService emailTemplateService;

	
	@RequestMapping("/")
	public String home() {
		return "User Management";
	}

	@RequestMapping(method = RequestMethod.POST, value = "/registerUser")
	public UserSignupResponse registerUser(@RequestBody UserSignupRequest request) {
		UserSignupResponse response = new UserSignupResponse();

		try {
			User user = new User();
			SecureData sd = new SecureData();
			String encryptedPassword = sd.encrypt(request.getUser().getPassword());
			user.setFirstName(request.getUser().getFirstName());
			user.setLastName(request.getUser().getLastName());
			user.setEmail(request.getUser().getEmail());
			user.setPassword(encryptedPassword);
			user.setPhoneNumber(request.getUser().getPhoneNumber());
			user.setUserName(request.getUser().getUserName());
			user.setBusinessName(request.getUser().getBusinessName());
			user.setRememberMe(request.getUser().isRememberMe());
			user.setProfileImageUrl("assets/img/boyUser.png");
			user.setCreatedDate(DateTimeUtil.getTodayString());
			User retUser = userMgmtService.saveUser(user);
			response.setUser(retUser);
			String uuidRefresh = UUID.randomUUID().toString().replace("-", "");
			response.setUserRefresh(uuidRefresh);
			UserActivity userActivity = new UserActivity();
			userActivity.setUserId(retUser.getId());
			userActivity.setUserRefreshToken(uuidRefresh);
			userMgmtService.saveAvtivity(userActivity);
			logger.info("User Saved");			
			/* sending email 			*/
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("firstName", retUser.getFirstName());
			map.put("lastName", retUser.getLastName());
			map.put("userName", retUser.getUserName());
			String subject= "Your New Account for Skyrate";
			String emailBody = emailTemplateService.getEmailTemplate("registrationConfirm.vm",map);
			EmailMessage emailMessage = new EmailMessage();
			emailMessage.setToEmail(retUser.getEmail());
			emailMessage.setSubject(subject);
			emailMessage.setEmailBody(emailBody);
			emailService.sendEmail(retUser.getEmail(), subject, emailBody, null);
			//emailService.send2EmailQueue(emailMessage);
			logger.info("User registration email sent");
			
		} catch (Exception e) {
			logger.error("Signup Failed", e);
			response.setSuccess(false);
		}
		return response;
	}

	@RequestMapping(method = RequestMethod.POST, value = "/login")
	public LoginResponse login(@RequestBody LoginRequest request,HttpServletRequest servletRequest,
											HttpServletResponse res) {
		
		
		LoginResponse response = new LoginResponse();
		try {
			SecureData sd = new SecureData();
			String encryptedPassword = sd.encrypt(request.getPassword());
			User retUser = userMgmtService.login(request.getEmail(), encryptedPassword);
			response.setUserProfile(retUser);
			String uuid = UUID.randomUUID().toString().replace("-", "");
			response.setUserActive(uuid);
			String uuidRefresh = UUID.randomUUID().toString().replace("-", "");
			response.setUserRefresh(uuidRefresh);
			UserActivity userActivity = new UserActivity();
			userActivity.setUserId(retUser.getId());
			userActivity.setUserRefreshToken(uuidRefresh);
			if(request.isRememberMe())
			userActivity.setSecureToken(uuid);
			userMgmtService.saveAvtivity(userActivity);
			if(request.isRememberMe()){
				Cookie myCookie = new Cookie("name", uuid);
				myCookie.setPath("/");
				res.addCookie(myCookie);
			}
			
			
			if(retUser!=null){
				try{
					long roleId = userMgmtService.getUserRoleId(retUser.getId());
					response.setRoleId(roleId);
				}catch(Exception e){
					logger.error("roleId not available", e);
				}
			}
			else{
				response.setSuccess(false);
			}
		//	emailService.sendEmail("rajazekar@yahoo.com", "rajazekar@gmail.com", "Hi", "Hello");
			
		} catch (Exception e) {
			logger.error("login Failed", e);
			response.setSuccess(false);
		}
		return response;
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/usernameExists")
	public UserNameExistResponse userNameExist(@RequestBody UserNameExistRequest request){
		UserNameExistResponse response = new UserNameExistResponse();
		try{
			User user = userMgmtService.userNameExist(request.getUserName());
			if(user!=null)
				response.setUserNameExists(true);
			else
				response.setUserNameExists(false);
		}
		catch(Exception e){
			logger.error("invalid Username", e);
			response.setSuccess(false);
			
		}
		return response;
	}	
	@RequestMapping(method = RequestMethod.POST, value = "/forgotPassword")
	public ForgotPasswordResponse forgotPassword(@RequestBody ForgotPasswordRequest request){
		ForgotPasswordResponse response = new ForgotPasswordResponse();
		try{
			
			User user = userMgmtService.getUserByEmail(request.getUserName());
			SecureData sd = new SecureData();
			String clearPassword = sd.decrypt(user.getPassword());
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("firstName", user.getFirstName());
			map.put("lastName", user.getLastName());
			map.put("userName", user.getUserName());
			map.put("password", clearPassword );
			String subject = "Skyrate password assistance";
			String emailBody = emailTemplateService.getEmailTemplate("recoveryPassword.vm",map);
			EmailMessage emailMessage = new EmailMessage();
			emailMessage.setToEmail(user.getEmail());
			emailMessage.setSubject(subject);
			emailMessage.setEmailBody(emailBody);
			emailService.sendEmail(user.getEmail(), subject, emailBody, null);
			//emailService.send2EmailQueue(emailMessage);
			
		}
		catch(Exception e){
			logger.error("no username found" ,e);
			response.setSuccess(false);
		}
	
		return response;
	}
	@RequestMapping(method = RequestMethod.POST, value = "/forgotUsername")
	public ForgotUsernameResponse forgotUsername(@RequestBody ForgotUsernameRequest request){
		ForgotUsernameResponse response = new ForgotUsernameResponse();
		try{
			User user = userMgmtService.getUserByEmail(request.getEmail());

			response.setUsername(user.getUserName());
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("firstName", user.getFirstName());
			map.put("lastName", user.getLastName());
			map.put("userName", user.getUserName());
			String subject= "Your Username for Spice Corner";
			String emailBody = emailTemplateService.getEmailTemplate("recoveryUsername.vm",map);
			EmailMessage emailMessage = new EmailMessage();
			emailMessage.setToEmail(user.getEmail());
			emailMessage.setSubject(subject);
			emailMessage.setEmailBody(emailBody);
			emailService.sendEmail(user.getEmail(), subject, emailBody, null);
			//emailService.send2EmailQueue(emailMessage);
			
		}
		catch(Exception e){
			logger.error("invalid email", e);
			response.setSuccess(false);
		}
		return response;
}
	@RequestMapping(method = RequestMethod.POST, value = "/changePassword")
	public ChangePasswordResponse chanagePassword(@RequestBody ChangePasswordRequest request){
		ChangePasswordResponse response = new ChangePasswordResponse();
		try{
			SecureData sd = new SecureData();
			String encryptedPassword = sd.encrypt(request.getOldPassword());
			User user = userMgmtService.changePassword(request.getUserId(), encryptedPassword );
			if(user==null){
				response.setUserErrorMsg("User & Password not matching!");
				response.setSuccess(false);
				return response;
			}
			SecureData sd1 = new SecureData();
			String password = sd1.encrypt(request.getNewPassword());
			user.setPassword(password);
			user = userMgmtService.saveUser(user);
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("firstName", user.getFirstName());
			map.put("lastName", user.getLastName());
			map.put("userName", user.getUserName());
			String subject= "Skyrate Password changed";
			String emailBody = emailTemplateService.getEmailTemplate("changePassword.vm",map);
			EmailMessage emailMessage = new EmailMessage();
			emailMessage.setToEmail(user.getEmail());
			emailMessage.setSubject(subject);
			emailMessage.setEmailBody(emailBody);
			emailService.sendEmail(user.getEmail(), subject, emailBody, null);
			//emailService.send2EmailQueue(emailMessage);
			logger.info("successfully update");
		}
		catch(Exception e){
			logger.error("invalid password", e);
			response.setSuccess(false);
		}		return response;
	}
	@RequestMapping(method = RequestMethod.POST, value = "/updateUserProfile")
	public UpdateUserProfileResponse updateUserProfile(@RequestBody UpdateUserProfileRequest request){
		UpdateUserProfileResponse response = new UpdateUserProfileResponse();
		try{

			String content = ""; 
			User user = userMgmtService.getUserById(request.getUser().getId());
			if(!request.getUser().getEmail().equals(user.getEmail()))
				 content = "Email : <b>"+request.getUser().getEmail()+"</b>. <br>";
			if(!request.getUser().getFirstName().equals(user.getFirstName()))
				content+= "First Name : <b>"+request.getUser().getFirstName()+"</b>. <br>";
			if(!request.getUser().getLastName().equals(user.getLastName()))
				content+= "Last Name : <b>"+request.getUser().getLastName()+"</b>. <br>";
			if(!request.getUser().getPhoneNumber().equals(user.getPhoneNumber()))
				content+= "PhoneNumber : <b>"+request.getUser().getPhoneNumber()+"</b>. <br>";
			if(!request.getUser().getBusinessName().equals(user.getBusinessName()))
				content+= "Business Name : <b>"+request.getUser().getBusinessName()+"</b>. <br>";
			user.setEmail(request.getUser().getEmail());
			user.setFirstName(request.getUser().getFirstName());
			user.setLastName(request.getUser().getLastName());
			user.setPhoneNumber(request.getUser().getPhoneNumber());
			user.setBusinessName(request.getUser().getBusinessName());
			user.setProfileImageUrl(request.getUser().getProfileImageUrl());
			userMgmtService.saveUser(user);
		
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("firstName", user.getFirstName());
			map.put("lastName", user.getLastName());
			map.put("userName", user.getUserName());
			map.put("content", content);
			String subject= "Skyrate Profile updated";
			String emailBody = emailTemplateService.getEmailTemplate("userProfileUpdate.vm",map);
			EmailMessage emailMessage = new EmailMessage();
			emailMessage.setToEmail(user.getEmail());
			emailMessage.setSubject(subject);
			emailMessage.setEmailBody(emailBody);
			emailService.sendEmail(user.getEmail(), subject, emailBody, null);
			logger.info("successfully update");
			user.setPassword("");
			response.setUser(user);
		}
		catch(Exception e){
			logger.error("invalid userId",e);
			response.setSuccess(false);
		}
		return response;
}
	
	@RequestMapping(method = RequestMethod.POST, value = "/emailIdExist")
	public EmailIdExistResponse emailIdExist(@RequestBody EmailIdExistRequest request){
		EmailIdExistResponse response = new EmailIdExistResponse();
		try{
			User user = userMgmtService.emailIdExist(request.getEmail());
			if(user != null){
				response.setEmailIdExist(true);
			}
				
			else
				response.setEmailIdExist(false);
		}
		catch(Exception e){
			logger.error("invalid EmailId", e);
			response.setSuccess(false);
		}
		
		return response;
	}
	
	@RequestMapping( method = RequestMethod.POST, value = "/getUserActive")
	public LoginResponse getUserCookies(@RequestBody CookieRequest request){
		LoginResponse response = new LoginResponse();
		try{
			UserActivity userActive  = new UserActivity();
			if(request.getValue() != null)
				userActive = userMgmtService.getActivityByToken(request.getValue());
			else
				userActive = userMgmtService.getActivityByRefreshToken(request.getRefreshToken());
			if(userActive != null){
				User user = userMgmtService.getUserById(userActive.getUserId());
				response.setUserProfile(user);
				try{
					long roleId = userMgmtService.getUserRoleId(user.getId());
					response.setRoleId(roleId);
				}catch(Exception e){
					logger.error("roleId not available", e);
				}
			}
			
			else{
				response.setSuccess(false);
				
			}
		}
		catch(Exception e){
			logger.error("active failed",e);
			response.setSuccess(false);
			
		}
		return response;
	}
	
	@RequestMapping(method = RequestMethod.POST, value="/getUserCount")
	public UserCountResponse getUserCount(@RequestBody UserCountRequest request){
		UserCountResponse response = new UserCountResponse();
		try{
			String dateString =  DateTimeUtil.getTodayString();
			if(request.getDate().equals("Today"))
				dateString = DateTimeUtil.getTodayString();
			if(!request.getDate().isEmpty() && !request.getDate().equals("Today") && !request.getDate().equals("Last 30 days"))
				dateString = DateTimeUtil.changeStringFormat(request.getDate());
			if(request.getDate().equals("Last 30 days")){
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.MONTH, -1);
				Date result = cal.getTime();
				dateString =DateTimeUtil.changeDatetoString(result);
			}
			UserCount count = userMgmtService.getUserCount(dateString);
			response.setCount(count.getCount());
			logger.info("get user count");
		}
		catch(Exception e){
			logger.error("count failed");
			response.setSuccess(false);
		}
		return response;
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/getUserById")
	public UserDetailResponse getUserById(@RequestBody UserByIdRequest request){
		UserDetailResponse response = new UserDetailResponse();
		try{
			User user = userMgmtService.getUser(request.getId());
			response.setUser(user);
			logger.info("user details");
		}
		catch(Exception e){
			logger.error("user failed",e);
			response.setSuccess(false);
		}
		return response;
	}
}