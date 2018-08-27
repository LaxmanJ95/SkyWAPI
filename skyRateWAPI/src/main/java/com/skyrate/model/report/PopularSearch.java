package com.skyrate.model.report;

import com.skyrate.model.dbentity.Business;

public class PopularSearch extends Business{

	int totalHits;
	String ratingAverage;
	double overallRating;
	int reviewCount;
	
	public int getTotalHits() {
		return totalHits;
	}
	public void setTotalHits(int totalHits) {
		this.totalHits = totalHits;
	}
	public String getRatingAverage() {
		return ratingAverage;
	}
	public void setRatingAverage(String ratingAverage) {
		this.ratingAverage = ratingAverage;
	}
	public double getOverallRating() {
		return overallRating;
	}
	public void setOverallRating(double overallRating) {
		this.overallRating = overallRating;
	}
	public int getReviewCount() {
		return reviewCount;
	}
	public void setReviewCount(int reviewCount) {
		this.reviewCount = reviewCount;
	}
	
	
	
}
