package com.skyrate.rest;

import java.io.FileReader;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
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
import com.skyrate.clib.util.SKYRATEUtil;
import com.skyrate.model.business.ActiveRequest;
import com.skyrate.model.business.AddBusinessRequest;
import com.skyrate.model.business.ApprovalRequest;
import com.skyrate.model.business.BookmarkDetails;
import com.skyrate.model.business.BusinessCategory;
import com.skyrate.model.business.BusinessCategoryMappingRequest;
import com.skyrate.model.business.BusinessClaimed;
import com.skyrate.model.business.BusinessCount;
import com.skyrate.model.business.BusinessDetailRequest;
import com.skyrate.model.business.BusinessDetails;
import com.skyrate.model.business.BusinessDetailsResponse;
import com.skyrate.model.business.BusinessExistsRequest;
import com.skyrate.model.business.BusinessExistsResponse;
import com.skyrate.model.business.BusinessResponse;
import com.skyrate.model.business.BusinessSummaryDetails;
import com.skyrate.model.business.CategoryList;
import com.skyrate.model.business.CategoryResponse;
import com.skyrate.model.business.ClaimBusinessRequest;
import com.skyrate.model.business.ClaimBusinessResponse;
import com.skyrate.model.business.ClaimedBusinessResponse;
import com.skyrate.model.business.FavoriteResponse;
import com.skyrate.model.business.PaginationRequest;
import com.skyrate.model.business.PopularSearchRequest;
import com.skyrate.model.business.PopularSearchResponse;
import com.skyrate.model.business.SearchBusinessRequest;
import com.skyrate.model.business.SearchBusinessResponse;
import com.skyrate.model.dbentity.Business;
import com.skyrate.model.dbentity.BusinessCategoryMapping;
import com.skyrate.model.dbentity.BusinessClaimMapping;
import com.skyrate.model.dbentity.BusinessRatingSummary;
import com.skyrate.model.dbentity.Category;
import com.skyrate.model.dbentity.RejectedBusiness;
import com.skyrate.model.dbentity.User;
import com.skyrate.model.dbentity.UserRoleMapping;
import com.skyrate.model.email.EmailMessage;
import com.skyrate.model.report.PopularSearch;
import com.skyrate.model.report.TopSearchRequest;
import com.skyrate.service.BusinessService;
import com.skyrate.service.RatingAndReviewService;
import com.skyrate.service.ReportService;
import com.skyrate.service.UserMgmtService;

@RestController
@RequestMapping("/business")
@CrossOrigin(maxAge=3600)
public class BusinessController {

	private static final Logger logger =  LoggerFactory.getLogger(BusinessController.class);
	
	@Autowired
	BusinessService businessService;
	
	@Autowired
	RatingAndReviewService ratingReviewService;
	
	@Autowired
	UserMgmtService userMgmtService;
	
	@Autowired
	EmailService emailService;
	@Autowired
	private EmailTemplateService emailTemplateService;
	
	@Autowired
	ReportService reportService;
	
	@RequestMapping(method = RequestMethod.POST, value="/searchBusiness")
	public SearchBusinessResponse searchBusiness(@RequestBody SearchBusinessRequest request){
		SearchBusinessResponse response = new SearchBusinessResponse();
		try{
			System.out.println("reqquest "+request.getCountry());
			if(request.getName() != null ){
				List<BusinessDetails> business = null;
				if(!request.getCategory().isEmpty()){
					 business = businessService.searchBusiness(request.getCountry(),request.getCategory(),request.getName(),request.getRoleId());
				}
				else{
					business = businessService.searchForAll(request.getCountry(),request.getName(),request.getRoleId());
				}				
				BusinessCount count = businessService.businessCount(request.getCountry(),request.getCategory(), request.getName(), request.getRoleId());
				System.out.println("count "+count.getCount());
				response.setBusiness(business);
				response.setCount(count.getCount());
			}
			logger.info("search business");
		}
		catch(Exception e){
			logger.error("search failed",e);
			response.setSuccess(false);
		}
		return response;
	}
	
	@RequestMapping(method = RequestMethod.POST, value="/getBusinessById")
	public BusinessDetailsResponse getBusiness(@RequestBody BusinessDetailRequest request){
		BusinessDetailsResponse response = new BusinessDetailsResponse();
		try{
			BusinessDetails business = businessService.getBusinessById(request.getId());
			BusinessRatingSummary ratingSummary = ratingReviewService.getRatingSummaryByBusinessId(request.getId());
			if(ratingSummary != null){
				response.setBusinessRating(ratingSummary);
			}
			response.setBusiness(business);
			List<BusinessCategory> mapping = businessService.getBusinessCategory(request.getId());
			response.setBusinessCategory(mapping);
			List<CategoryList> category = businessService.getCategoryByBusiness(request.getId());
			BusinessClaimMapping businessClaim = businessService.getClaimMappingById(request.getId());
			if(businessClaim != null)
				response.setBusinessClaim(businessClaim);
			
			response.setCategory(category);
			
			logger.info("Business By Id");
		}
		catch(Exception e){
			logger.error("Business by Id Failed",e);
			response.setSuccess(false);
		}
		return response;
	}
	
	@RequestMapping(method = RequestMethod.POST, value="/getBusinessByPagination")
	public SearchBusinessResponse getBusinessByPagination(@RequestBody PaginationRequest request){
		SearchBusinessResponse response = new SearchBusinessResponse();
		try{
			 int from=1;
				int to=10;
				for(int i=1;i<=request.getValue();i++){
					if(i==1){
						from=0;
						to=10;
					}
					else{
						from+=10;
						to+=10;
					}
				}
			List<BusinessDetails> business = businessService.getBusinessByPagination(from, to,request.getCategory(),request.getSearch(),request.getRoleId());
			response.setBusiness(business);
			logger.info(" business pagination");
		}
		catch(Exception e){
			logger.error("business pagination failed",e);
			response.setSuccess(false);
		}
		return response;
	}
	
	@RequestMapping(method = RequestMethod.POST, value ="/popularSearches")
	public PopularSearchResponse popularSearch(@RequestBody PopularSearchRequest request){
		PopularSearchResponse response = new PopularSearchResponse();
		try{
			List<PopularSearch> search = null;
			if(request.getEnablePopular() == 1){
				search = reportService.getPopularSearch(request.getCategory());
			}
			else{
				search = reportService.getTrendingBusiness();
			}
			response.setPopularSearch(search);
			logger.info("popular");
		}
		catch(Exception e){
			logger.error("popular failed",e);
			response.setSuccess(false);
		}
		return response;
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/addUpdateBusiness")
	public SuccessIDResponse addUpdateBusiness(@RequestBody AddBusinessRequest request){
		SuccessIDResponse response = new SuccessIDResponse();
		try{
			Business business = new Business();
			if(request.getBusiness().getId() != 0){
			  business = businessService.getBusiness(request.getBusiness().getId());
			}
			if(business == null){
				business = new Business();
				business.setActive(1);
			}
			business.setActive(request.getBusiness().getActive());
			business.setImageUrl(request.getBusiness().getImageUrl());
			business.setAddress(request.getBusiness().getAddress());
			business.setCountry(request.getBusiness().getCountry());
			business.setCity(request.getBusiness().getCity());
			business.setState(request.getBusiness().getState());
			business.setZip(request.getBusiness().getZip());
			business.setName(request.getBusiness().getName());
			business.setCapabilities(request.getBusiness().getCapabilities());
			business.setOverview(request.getBusiness().getOverview());
			business.setCertificateHoldingOffice(request.getBusiness().getCertificateHoldingOffice());
			business.setCertificateNumber(request.getBusiness().getCertificateNumber());
			business.setWebsite(request.getBusiness().getWebsite());
			business.setDesignatorCode(request.getBusiness().getDesignatorCode());
//			BeanUtils.copyProperties(request.getBusiness(), business);
			businessService.saveBusiness(business);
			if(request.getBusiness().getId() == 0){
				for(CategoryList mapping : request.getCategory()){
						BusinessCategoryMapping category = new BusinessCategoryMapping();
						category.setCategoryId(mapping.getId());
						category.setBusinessId(business.getId());
						businessService.saveCategoryMpping(category);
				}
				BusinessClaimMapping businessClaimMapping = new BusinessClaimMapping();
				businessClaimMapping.setBusinessId(business.getId());
				businessClaimMapping.setUserId(request.getUserId());
				businessClaimMapping.setCreatedDate(new Date());
				businessService.saveClaimMapping(businessClaimMapping);
			}
			else{
				List<CategoryList> categoryList = businessService.getCategoryByBusiness(request.getBusiness().getId());
					for(CategoryList newCategory : request.getCategory()){
							BusinessCategoryMapping categoryMapping = businessService.getByCategoryAndBusiness(newCategory.getId(), request.getBusiness().getId());
							if(categoryMapping == null){
								categoryMapping = new BusinessCategoryMapping();
								categoryMapping.setCategoryId(newCategory.getId());
								categoryMapping.setBusinessId(business.getId());
								businessService.saveCategoryMpping(categoryMapping);
							}
						}
	
				for(CategoryList deleteCategory : request.getDeleteCategory()){
							BusinessCategoryMapping categoryMapping =  businessService.getByCategoryAndBusiness(deleteCategory.getId(), request.getBusiness().getId());
							if(categoryMapping != null)
							businessService.deleteCategoryMapping(categoryMapping.getId());
				}
				
			}
			
			logger.info("get and update business");
		}
		catch(Exception e){
			logger.error("failed business",e);
			response.setSuccess(false);
		}
		return response;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/getFavorite/{userId}")
	public FavoriteResponse getFavorite(@PathVariable int userId){
		FavoriteResponse response = new FavoriteResponse();
		try{
			List<BookmarkDetails> bookmark = businessService.getBookmarkDetails(userId);
			response.setBookmark(bookmark);
			logger.info("Bookmark details");
		}
		catch(Exception e){
			logger.error("failed bookmark ",e);
			response.setSuccess(false);
		}
		return response;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/getCategory")
	public CategoryResponse getCategory(){
		CategoryResponse response = new CategoryResponse();
		try{
			List<CategoryList> category = businessService.getCategory();
			response.setCategory(category);
			logger.info("get category");
		}
		catch(Exception e){
			logger.error("failed category ",e);
			response.setSuccess(false);
		}
		return response;
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/claimBusiness")
	public ClaimBusinessResponse claimBusiness(@RequestBody ClaimBusinessRequest request){
		ClaimBusinessResponse response = new ClaimBusinessResponse();
		try{
			BusinessClaimMapping businessClaim =  new BusinessClaimMapping();
			businessClaim.setBusinessId(request.getBusinessId());
			businessClaim.setUserId(request.getUserId());
			businessClaim.setCreatedDate(new Date());
			businessService.saveClaimMapping(businessClaim);
			response.setClaimBusiness(businessClaim);
			Business business = businessService.getBusiness(request.getBusinessId());
//			UserRoleMapping userRole = userMgmtService.getByRoleId(1);
			 
			Properties properties = new Properties();
			String adminProperties;
			String adminMail= "";
			try{
				adminProperties = SKYRATEUtil.getConfigDIR() + "/adminConfig.properties";
				properties.load(new FileReader(adminProperties));
				adminMail = properties.getProperty("AdminEmail");
				}
				catch(Exception e){
					e.printStackTrace();
				}
			System.out.println("email "+adminMail);
//			User adminUser = userMgmtService.getUserById(userRole.getUserId());
			User user = userMgmtService.getUserById(request.getUserId());
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("business", business.getName() );
			map.put("user", user.getFirstName());
			String subject= "Business Claim at Skyrate";
			String emailBody = emailTemplateService.getEmailTemplate("claimBusiness.vm",map);
			EmailMessage emailMessage = new EmailMessage();
//			emailMessage.setToEmail(adminUser.getEmail());
			emailMessage.setSubject(subject);
			emailMessage.setEmailBody(emailBody);
			emailService.sendEmail(adminMail, subject, emailBody, null);
			logger.info("Business Claimed");
		}
		catch(Exception e){
			logger.error("Claimed Failed",e);
			response.setSuccess(false);
		}
		return response;
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/businessNameExists")
	public BusinessExistsResponse businessNameExists(@RequestBody BusinessExistsRequest request){
		BusinessExistsResponse response = new BusinessExistsResponse();
		try{
			Business business = businessService.businessExists(request.getBusiness());
			if(business != null){
				response.setBusinessNameExists(true);
			}
			else{
				response.setBusinessNameExists(false);
			}
			logger.info("business exists");
		}
		catch(Exception e){
			logger.error("Exists Failed",e);
			response.setSuccess(false);
		}
		return response;
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/getClaimed")
	public ClaimedBusinessResponse getBusinessClaimed(@RequestBody TopSearchRequest request){
		ClaimedBusinessResponse response = new ClaimedBusinessResponse();
		try{
			String date = DateTimeUtil.getTodayString();
			if(!request.getFeedbackDate().isEmpty() && !request.getFeedbackDate().equals("Today") && !request.getFeedbackDate().equals("Last 30 days"))
			 date = DateTimeUtil.changeStringFormat(request.getFeedbackDate());
			if(request.getFeedbackDate().equals("Last 30 days")){
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.MONTH, -1);
				Date result = cal.getTime();
				System.out.println("nw date "+result);
				date =DateTimeUtil.changeDatetoString(result);
			}
			List<BusinessClaimed> businessClaimed = businessService.getBusinessClaimed(date,0,0);
			BusinessCount businessCount = businessService.getClaimedCount(date);
			response.setBusinessClaimed(businessClaimed);
			response.setCount(businessCount.getCount());
			logger.info("Business Claimed");
		}
		catch(Exception e){
			logger.error("Claimed Failed",e);
			response.setSuccess(false);
		}
		return response;
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/getClaimedPagination")
	public ClaimedBusinessResponse getBusinessClaimedPagination(@RequestBody TopSearchRequest request){
		ClaimedBusinessResponse response = new ClaimedBusinessResponse();
		try{
			int from=1;
			int to=10;
			for(int i=1;i<=request.getClaimedValue();i++){
				if(i==1){
					from=0;
					to=10;
				}
				else{
					from+=10;
					to+=10;
				}
			}
			String date = DateTimeUtil.getTodayString();
			if(!request.getFeedbackDate().isEmpty() && !request.getFeedbackDate().equals("Today") && !request.getFeedbackDate().equals("Last 30 days"))
			 date = DateTimeUtil.changeStringFormat(request.getFeedbackDate());
			if(request.getFeedbackDate().equals("Last 30 days")){
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.MONTH, -1);
				Date result = cal.getTime();
				System.out.println("nw date "+result);
				date =DateTimeUtil.changeDatetoString(result);
			}
			List<BusinessClaimed> businessClaimed = businessService.getBusinessClaimed(date,from,to);
			response.setBusinessClaimed(businessClaimed);
			logger.info("Business Claimed");
		}
		catch(Exception e){
			logger.error("Claimed Failed",e);
			response.setSuccess(false);
		}
		return response;
	}
	
	
	@RequestMapping(method = RequestMethod.POST, value = "/updateApproval")
	public SuccessIDResponse updateApproval(@RequestBody ApprovalRequest request){
		SuccessIDResponse response = new SuccessIDResponse();
		try{
			String approved = "Rejected";
			BusinessClaimMapping businessClaim = businessService.getClaimMappingById(request.getBusinessId());
			Business business = businessService.getBusiness(request.getBusinessId());
			if(request.getApproval() == 0){
				RejectedBusiness rejected = new RejectedBusiness();
				rejected.setBusinessId(businessClaim.getBusinessId());
				rejected.setUserId(businessClaim.getUserId());
				rejected.setCreatedDate(new Date());
				businessService.saveReject(rejected);
				business.setUserId(0);
				businessService.saveBusiness(business);
				businessService.deleteClaimed(businessClaim.getId());
			}
			else{
			businessClaim.setApproval(request.getApproval());
			businessClaim.setLastUpdatedDate(new Date());
			businessService.saveClaimMapping(businessClaim);
			approved="Approved";
			business.setActive(0);
			business.setUserId(businessClaim.getUserId());
			businessService.saveBusiness(business);
			}
			
			User user = userMgmtService.getUserById(businessClaim.getUserId());
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("firstName", user.getFirstName());
			map.put("lastName", user.getLastName());
			map.put("business", business.getName() );
			map.put("approved", approved);
			String subject= "Business Claim at Skyrate";
			String emailBody = emailTemplateService.getEmailTemplate("approval.vm",map);
			EmailMessage emailMessage = new EmailMessage();
			emailMessage.setToEmail(user.getEmail());
			emailMessage.setSubject(subject);
			emailMessage.setEmailBody(emailBody);
			emailService.sendEmail(user.getEmail(), subject, emailBody, null);	
			logger.info("get approved");
		}
		catch(Exception e){
			logger.error("APproval Failed",e);
			response.setSuccess(false);
		}
		return response;
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/setBusinessActive")
	public SuccessIDResponse setBusinessActive(@RequestBody ActiveRequest request){
		SuccessIDResponse response = new SuccessIDResponse();
		try{
			Business business = businessService.getBusiness(request.getBusinessId());
			business.setActive(request.getActive());
			businessService.saveBusiness(business);
			logger.info("Set active per request");
		}
		catch(Exception e){
			logger.error("Active Failed",e);
			response.setSuccess(false);
		}
		return response;
	}
}
