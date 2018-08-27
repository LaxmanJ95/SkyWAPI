package com.skyrate.model.message;

import java.util.List;

import com.skyrate.clib.model.BaseResponse;

public class MessageListResponse extends BaseResponse{

	List<MessagesById> messages;

	public List<MessagesById> getMessages() {
		return messages;
	}

	public void setMessages(List<MessagesById> messages) {
		this.messages = messages;
	}
	
	
}
