package com.skyrate.model.message;

public class MessageRequest {

	int from;
	int to;
	int covnId;
	String message;
	int reviewId;
	public int getFrom() {
		return from;
	}
	public void setFrom(int from) {
		this.from = from;
	}
	public int getTo() {
		return to;
	}
	public void setTo(int to) {
		this.to = to;
	}
	public int getCovnId() {
		return covnId;
	}
	public void setCovnId(int covnId) {
		this.covnId = covnId;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public int getReviewId() {
		return reviewId;
	}
	public void setReviewId(int reviewId) {
		this.reviewId = reviewId;
	}
	
	
}
