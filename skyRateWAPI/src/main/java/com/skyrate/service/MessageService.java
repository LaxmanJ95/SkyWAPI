package com.skyrate.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.skyrate.dao.ConversationMappingRepository;
import com.skyrate.dao.MessageQuery;
import com.skyrate.dao.MessengerRepository;
import com.skyrate.model.dbentity.ConversationMapping;
import com.skyrate.model.dbentity.Messenger;
import com.skyrate.model.message.MessagesById;
import com.skyrate.model.message.ReadCount;

@Service
@Transactional
public class MessageService {

	@Autowired
	MessageQuery messageQuery;
	
	@Autowired
	ConversationMappingRepository conversationMappingRepository;
	
	@Autowired
	MessengerRepository messengerRepository;
	
	public ConversationMapping conversationMapping(int messenger1,int messenger2){
		return this.messageQuery.conversationMapping(messenger1, messenger2);
	}
	public ConversationMapping saveConversationMapping(ConversationMapping conversationMapping){
		return this.conversationMappingRepository.save(conversationMapping);
	}
	public Messenger saveMessenger(Messenger messenger){
		return this.messengerRepository.save(messenger);
	}
	public List<MessagesById> getMessagesById(int conversationId){
		return this.messageQuery.getMessagesById(conversationId);
	}
	public List<MessagesById> getInbox(int id){
		return this.messageQuery.getInbox(id);
	}
	public void updateRead(int conversationId, int toId){
		this.messageQuery.updateRead(conversationId, toId);
	}
	public List<ReadCount> getReadCount(int fromId){
		return this.messageQuery.getReadCount(fromId);
	}
}
