package com.skyrate.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skyrate.dao.BusinessCategoryMappingRepository;
import com.skyrate.dao.BusinessClaimMappingRepository;
import com.skyrate.dao.BusinessQuery;
import com.skyrate.dao.BusinessRepository;
import com.skyrate.dao.CategoryRepository;
import com.skyrate.dao.RejectedBusinessRepository;
import com.skyrate.model.business.BookmarkDetails;
import com.skyrate.model.business.BusinessCategory;
import com.skyrate.model.business.BusinessClaimed;
import com.skyrate.model.business.BusinessCount;
import com.skyrate.model.business.BusinessDetails;
import com.skyrate.model.business.BusinessSummaryDetails;
import com.skyrate.model.business.CategoryList;
import com.skyrate.model.dbentity.Business;
import com.skyrate.model.dbentity.BusinessCategoryMapping;
import com.skyrate.model.dbentity.BusinessClaimMapping;
import com.skyrate.model.dbentity.Category;
import com.skyrate.model.dbentity.RejectedBusiness;

@Service
@Transactional
public class BusinessService {
	
	@Autowired
	BusinessQuery businessQuery;
	
	@Autowired
	BusinessRepository businessRepository;
	
	@Autowired
	BusinessClaimMappingRepository businessClaimMappingRepository;
	
	@Autowired
	CategoryRepository categoryRepository;
	
	@Autowired
	BusinessCategoryMappingRepository businessCategoryMappingRepository;
	
	@Autowired
	RejectedBusinessRepository rejectedBusinessRepository;
	
	public List<BusinessDetails> searchBusiness(String country,String category,String name, int roleId){
		return this.businessQuery.searchBusiness(country,category,name,roleId);
	}
	public List<BusinessDetails> searchForAll(String country, String name, int roleId){
		return this.businessQuery.searchBusinessForAll(country, name, roleId);
	}
	public List<BusinessDetails> searchPageBusiness(String category,String name, int roleId){
		return this.businessQuery.searchPageBusiness(category,name,roleId);
	}
	public BusinessDetails getBusinessById(int id){
		return this.businessQuery.getBusinessById(id);
	}
	public Business getBusiness(int id){
		return this.businessRepository.findById(id);
	}
	public List<BusinessCategory> getBusinessCategory(int businessId){
		return this.businessQuery.getBusinessCategory(businessId);
	}
	public Business saveBusiness(Business business){
		return this.businessRepository.save(business);
	}
	public BusinessCount businessCount(String country, String category, String search, int roleId){
		return this.businessQuery.getBusinessCount(country, category, search, roleId);
	}
	public List<BusinessDetails> getBusinessByPagination(int from, int to,String category,String search,int roleId){
		return this.businessQuery.getBusinessPagination(from, to,category, search,roleId);
	}
	public BusinessSummaryDetails businessSummary(int businessId){
		return this.businessQuery.getBusinessSummary(businessId);
	}
	public List<BookmarkDetails> getBookmarkDetails(int userId){
		return this.businessQuery.getBookmarkDetails(userId);
	}
	public List<CategoryList> getCategory(){
		return this.businessQuery.getCategoryList();
	}
	public List<CategoryList> getCategoryByBusiness(int id){
		return this.businessQuery.getCategoryByBusiness(id);
	}
	public BusinessCategoryMapping saveCategoryMpping(BusinessCategoryMapping categoryMapping){
		return this.businessCategoryMappingRepository.save(categoryMapping);
	}
	public BusinessCategoryMapping getByCategoryAndBusiness(int categoryId, int businessId){
		return this.businessCategoryMappingRepository.findByCategoryIdAndBusinessId(categoryId, businessId);
	}
	public void deleteCategoryMapping(int id){
		this.businessQuery.deleteMapping(id);
	}
	
	public BusinessClaimMapping saveClaimMapping(BusinessClaimMapping businessClaim){
		return this.businessClaimMappingRepository.save(businessClaim);
	}
	public BusinessClaimMapping getClaimMappingById(int id){
		return this.businessClaimMappingRepository.findByBusinessId(id);
	}
	public Business businessExists(String name){
		return this.businessRepository.findByName(name);
	}
	public List<BusinessClaimed> getBusinessClaimed(String date, int from, int to){
		return this.businessQuery.getBusinessClaimed(date, from, to);
	}
	public BusinessCount getClaimedCount(String date){
		return this.businessQuery.getBusinessClaimedCount(date);
	}
	public RejectedBusiness saveReject(RejectedBusiness reject){
		return this.rejectedBusinessRepository.save(reject);
	}
	public void deleteClaimed(int id){
		System.out.println("rejected id "+id);
		 this.businessQuery.deleteClaimed(id);
	}
}
