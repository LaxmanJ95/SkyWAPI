package com.skyrate.model.message;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.skyrate.model.dbentity.Messenger;

public class MessagesById extends Messenger{

	String fromName;
	String toName;
	int readCount;
	@JsonFormat(pattern = "MMM dd HH:mm a")
	Date recentDate;
	
	
	public String getFromName() {
		return fromName;
	}
	public void setFromName(String fromName) {
		this.fromName = fromName;
	}
	public String getToName() {
		return toName;
	}
	public void setToName(String toName) {
		this.toName = toName;
	}
	public Date getRecentDate() {
		return recentDate;
	}
	public void setRecentDate(Date recentDate) {
		this.recentDate = recentDate;
	}
	public int getReadCount() {
		return readCount;
	}
	public void setReadCount(int readCount) {
		this.readCount = readCount;
	}
	
	
}
