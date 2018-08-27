package com.skyrate.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skyrate.clib.util.JSONUtil;
import com.skyrate.model.business.BookmarkDetails;
import com.skyrate.model.business.BusinessCategory;
import com.skyrate.model.business.BusinessClaimed;
import com.skyrate.model.business.BusinessCount;
import com.skyrate.model.business.BusinessDetails;
import com.skyrate.model.business.BusinessSummaryDetails;
import com.skyrate.model.business.CategoryList;
import com.skyrate.model.dbentity.Business;

@Service
@Transactional
public class BusinessQuery {

	@Autowired
	JdbcTemplate jdbcTemplate;
	public BusinessDetails getBusinessById(int id){
		String query = "select business.* from business "
				+"  where business.id="+id;
		
		BusinessDetails  business = (BusinessDetails) jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper(BusinessDetails.class));
		return business;
	}
	
	public List<BusinessDetails> searchBusiness(String country,String category,String name, int roleId){
		System.out.println("country "+country);
		String query = "select business.*,business_category_mapping.CATEGORY_ID,business_category_mapping.BUSINESS_ID,category.DESCRIPTION, "
						+ " CASE WHEN business_rating_summary.OVERALL_RATING is NULL THEN 0 ELSE business_rating_summary.OVERALL_RATING END AS OVERALL_RATING  from business_category_mapping "
						+" inner join business on business.id = business_category_mapping.BUSINESS_ID "
						+" inner join category on category.id = business_category_mapping.CATEGORY_ID "
						+" left join business_rating_summary  on business_rating_summary.BUSINESS_ID = business_category_mapping.BUSINESS_ID "
						+" where  ";
		if(country != null && !country.isEmpty())
			query+=" business.country = '"+country+"' and";
		
		query+="  category.category like '%"+category+"%' and business.name like '%"+name+"%' ";
		if(roleId != 2 && roleId != 1){
			query+=" and business.ACTIVE = 0 ";
		}
				if(!name.isEmpty())	{	
					query+="  order by business.name ASC limit 10";
					System.out.println("name "+name);
				}
				else
					query+="limit 10";

		List<BusinessDetails> business = jdbcTemplate.query(query, new BeanPropertyRowMapper(BusinessDetails.class));
		return business;
	}
	
	public List<BusinessDetails> searchBusinessForAll(String country, String name,int roleId){
		String query ="select business.*, "
					+ " CASE WHEN business_rating_summary.OVERALL_RATING is NULL THEN 0 ELSE business_rating_summary.OVERALL_RATING END AS OVERALL_RATING  from business "
					+" left join business_rating_summary  on business_rating_summary.BUSINESS_ID = business.ID "
					+" where business.name like '%"+name+"%'";
		if(!country.isEmpty() && country != null)
			query+=" and business.country ='"+country+"'";
		if(roleId != 2 && roleId != 1){
			query+=" and business.ACTIVE = 0 ";
		}
		query+=" limit 10";
		List<BusinessDetails> business = jdbcTemplate.query(query, new BeanPropertyRowMapper(BusinessDetails.class));
		return business;
	}
	
	public List<BusinessDetails> searchPageBusiness(String category,String name, int roleId){
		if(category == null){
			category = "";
		}
		String query = "select business.*, "+
				"CASE WHEN business_rating_summary.OVERALL_RATING is NULL THEN 0 ELSE business_rating_summary.OVERALL_RATING END AS OVERALL_RATING from business "+
						"left join business_rating_summary  on business_rating_summary.BUSINESS_ID = business.ID  where business.category like '%"+category+"%' and business.name like'%"+name+"%' ";
		if(roleId != 2 && roleId != 1)
			query+=" and business.active=0 ";
		query+="order by business.name ASC";
		List<BusinessDetails> business = jdbcTemplate.query(query, new BeanPropertyRowMapper(BusinessDetails.class));
		return business;
	}
	
	public List<BusinessDetails> getBusinessPagination(int from,int to,String category, String search, int roleId){
		String  query = null;
		System.out.println("category "+category);
		if(!category.isEmpty()){
			System.out.println("category if "+category);
			 query = "select business.*,business_category_mapping.CATEGORY_ID,business_category_mapping.BUSINESS_ID,category.DESCRIPTION, "
					+ " CASE WHEN business_rating_summary.OVERALL_RATING is NULL THEN 0 ELSE business_rating_summary.OVERALL_RATING END AS OVERALL_RATING  from business_category_mapping "
					+" inner join business on business.id = business_category_mapping.BUSINESS_ID "
					+" inner join category on category.id = business_category_mapping.CATEGORY_ID "
					+" left join business_rating_summary  on business_rating_summary.BUSINESS_ID = business_category_mapping.BUSINESS_ID "
					+" where category.category like '%"+category+"%' and business.name like '%"+search+"%'  ";
		if(roleId != 2 && roleId != 1){
			query+=" and business.ACTIVE = 0 ";
		}
			query+=" order by business.name ASC limit "+from+" , "+to;
		}
		else{
			System.out.println("category else "+category+" roleId "+roleId);
			query ="select business.*, "
					+ " CASE WHEN business_rating_summary.OVERALL_RATING is NULL THEN 0 ELSE business_rating_summary.OVERALL_RATING END AS OVERALL_RATING  from business "
					+" left join business_rating_summary  on business_rating_summary.BUSINESS_ID = business.ID "
					+" where business.name like '%"+search+"%'";
		if(roleId != 2 && roleId != 1){
			query+=" and business.ACTIVE = 0 ";
		}
		query+="  limit "+from+" , "+to;
		}
		
		List<BusinessDetails> business = jdbcTemplate.query(query, new BeanPropertyRowMapper(BusinessDetails.class));
		return business;
	}
	
	public BusinessSummaryDetails getBusinessSummary(int businessId){
		String query = "select business_rating_summary.*,bookmark.is_marked from business_rating_summary "
						+" left join boomark on bookmark.user_id = business_rating_summary.user_id where business_rating_summary.business_id="+businessId;
		BusinessSummaryDetails summary = (BusinessSummaryDetails) jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper(BusinessSummaryDetails.class));
		return summary;
	}
//	public BusinessDetails getBusinessDetailsById(int businessId){
//		String query = "select business_rating_summary.review_count,business_rating_summary.rating_count"
//	}
	
	public  List<BookmarkDetails> getBookmarkDetails(int userId){
		String query = "select business.*, bookmark.*, "
				+"CASE WHEN business_rating_summary.RATING_AVERAGE is NULL THEN 0 ELSE business_rating_summary.RATING_AVERAGE END AS RATING_AVERAGE, " 
						+"CASE WHEN business_rating_summary.PRICING_AVERAGE is NULL THEN 0 ELSE business_rating_summary.PRICING_AVERAGE END AS PRICING_AVERAGE, "
						+"CASE WHEN business_rating_summary.OVERALL_RATING is NULL THEN 0 ELSE business_rating_summary.OVERALL_RATING END AS OVERALL_RATING, "
                        +"CASE WHEN business_rating_summary.REVIEW_COUNT is NULL THEN 0 ELSE business_rating_summary.REVIEW_COUNT END AS REVIEW_COUNT,"
                        +"CASE WHEN business_rating_summary.IS_RECOMMEND is NULL THEN 0 ELSE business_rating_summary.IS_RECOMMEND END AS IS_RECOMMEND, "
                        +"CASE WHEN business_rating_summary.NOT_RECOMMEND is NULL THEN 0 ELSE business_rating_summary.NOT_RECOMMEND END AS NOT_RECOMMEND,"
						+"CASE WHEN business_rating_summary.DELIVERY_SPEED_AVG is NULL THEN 0 ELSE business_rating_summary.DELIVERY_SPEED_AVG END AS DELIVERY_SPEED_AVG from bookmark "
						+" inner join business on business.id = bookmark.business_id "
                        +" left join business_rating_summary on business_rating_summary.business_id = bookmark.business_id "
						+" where bookmark.user_id="+userId +" and business.active != 1";
		List<BookmarkDetails> bookmark = jdbcTemplate.query(query, new BeanPropertyRowMapper(BookmarkDetails.class));
		return bookmark;
	}
	
	
	public BusinessCount getBusinessCount(String country, String category, String search, int roleId){
		String query = null;
		System.out.println("count i query "+country);
		if(!category.isEmpty()){
			query = "select count(*) as count from business_category_mapping "
					+" inner join business on business.id = business_category_mapping.business_id "
					+" inner join category on category.id = business_category_mapping.category_id "
					+" left join business_rating_summary  on business_rating_summary.business_id = business_category_mapping.BUSINESS_ID "
					+" where category.category like '%"+category+"%' and business.name like '%"+search+"%'  ";
		}
		else{
			query = "select count(*) as count from business "
					+" left join business_rating_summary  on business_rating_summary.business_id = business.ID "
					+" where business.name like '%"+search+"%'";
		}
		if(!country.isEmpty() && country != null){
			query+=" and business.country='"+country+"'";
			System.out.println("count i query "+country);
		}
		if(roleId != 2 && roleId != 1){
			query+=" and business.ACTIVE = 0 ";
		}
		BusinessCount businessCount = (BusinessCount) jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper(BusinessCount.class));
		return businessCount;
	}
	
	public List<BusinessCategory> getBusinessCategory(int businessId){
		String query = "select business_category_mapping.*,category.DESCRIPTION from business_category_mapping "
						+ " inner join business on business.id = business_category_mapping.business_id "
						+ " inner join category on category.id = business_category_mapping.category_id "
						+ " where business_category_mapping.business_id = "+businessId;
		List<BusinessCategory> businessCategory = jdbcTemplate.query(query, new BeanPropertyRowMapper(BusinessCategory.class));
		return businessCategory;
	}
	
	public List<CategoryList> getCategoryList(){
		String query = "select category.id,category.category as item_name from category";
		List<CategoryList> businessCategory = jdbcTemplate.query(query, new BeanPropertyRowMapper(CategoryList.class));
		return businessCategory;
	} 
	
	public List<CategoryList> getCategoryByBusiness(int businessId){
		String query = "select category.id,category.category as item_name from category inner join business_category_mapping on "
						+" business_category_mapping.CATEGORY_ID = category.ID where business_category_mapping.BUSINESS_ID = "+businessId;
		List<CategoryList> businessCategory = jdbcTemplate.query(query, new BeanPropertyRowMapper(CategoryList.class));
		return businessCategory;
	}
	
	public void deleteMapping(int id){
		String query = " delete from business_category_mapping where id ="+id;
		jdbcTemplate.execute(query);
	}
	
	public List<BusinessClaimed> getBusinessClaimed(String date, int from, int to){
		String query = "select business.name,business.active,user.FIRST_NAME,user.email,user.phone_number, business_claim_mapping.* from business_claim_mapping "
					+ " inner join business on business.id = business_claim_mapping.BUSINESS_ID "
					+ " inner join user on user.id = business_claim_mapping.USER_ID "
					+ " where business_claim_mapping.created_date >= '"+date+"'  and business_claim_mapping.APPROVAL != 1 order by id desc ";
		if(from != 0){
			query+=" limit "+from+","+to;
		}
		else
			query+="limit 10";
		List<BusinessClaimed> businessClaimed = jdbcTemplate.query(query, new BeanPropertyRowMapper(BusinessClaimed.class));
		return businessClaimed;
	}
	
	public BusinessCount getBusinessClaimedCount(String date){
		String query = "select count(*) as count from business_claim_mapping "
						+ " inner join business on business.id = business_claim_mapping.BUSINESS_ID "
						+" inner join user on user.id = business_claim_mapping.USER_ID "
						+" where business_claim_mapping.created_date >= '"+date+"'  and business_claim_mapping.APPROVAL != 1 order by business_claim_mapping.created_date desc";
		
		BusinessCount businessCount = (BusinessCount) jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper(BusinessCount.class));
		return businessCount;
	}
	
	public void deleteClaimed(int id){
		String query = "delete from business_claim_mapping where id ="+id;
		jdbcTemplate.update(query);
	}
}
