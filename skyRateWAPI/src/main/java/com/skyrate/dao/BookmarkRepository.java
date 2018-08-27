package com.skyrate.dao;

import org.springframework.data.repository.CrudRepository;

import com.skyrate.model.dbentity.Bookmark;

public interface BookmarkRepository extends CrudRepository<Bookmark,Long>{

	Bookmark findByUserIdAndBusinessId(int userId,int businessId);
	Integer deleteByUserIdAndBusinessId(int userId,int businessId);
}
