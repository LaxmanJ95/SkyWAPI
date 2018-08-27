package com.skyrate.model.business;

import java.util.List;

import com.skyrate.model.dbentity.Business;
import com.skyrate.model.dbentity.BusinessCategoryMapping;

public class AddBusinessRequest {

	Business business;
	List<CategoryList> category;
	List<CategoryList> deleteCategory;
	int userId;
	
	public Business getBusiness() {
		return business;
	}

	public void setBusiness(Business business) {
		this.business = business;
	}

	public List<CategoryList> getCategory() {
		return category;
	}

	public void setCategory(List<CategoryList> category) {
		this.category = category;
	}

	public List<CategoryList> getDeleteCategory() {
		return deleteCategory;
	}

	public void setDeleteCategory(List<CategoryList> deleteCategory) {
		this.deleteCategory = deleteCategory;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	
}
