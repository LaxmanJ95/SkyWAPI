package com.skyrate.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.skyrate.model.dbentity.ConversationMapping;
import com.skyrate.model.message.MessagesById;
import com.skyrate.model.message.ReadCount;

@Service
@Transactional
public class MessageQuery {

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	public ConversationMapping conversationMapping(int messenger1, int messenger2){
		String query = "select * from conversation_mapping where MESSENGER1 = "+messenger1+" and MESSENGER2 = "+messenger2
						+ " or MESSENGER1 = "+messenger2+" and MESSENGER2 = "+messenger1; 
		try{
		ConversationMapping conversation =  (ConversationMapping) jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper(ConversationMapping.class));
		return conversation;
		}
		catch(EmptyResultDataAccessException e){
			return null;
		}
	}
	
	public List<MessagesById> getMessagesById(int conversationId){
		String query = "select messenger.*, A.first_name as From_Name, B.first_name as TO_name from messenger "
						+ " inner join user as A on A.id = messenger.FROM_ID "
						+ " inner join user as B on B.id = messenger.TO_ID where messenger.conversation_id = "+conversationId;
		List<MessagesById> messages = jdbcTemplate.query(query, new BeanPropertyRowMapper(MessagesById.class));
		return messages;
	}
	
	public List<MessagesById> getInbox(int id){
		String query = "select M1.*,M5.conversation_id as con,CASE WHEN M5.read_Count is NULL THEN 0 ELSE M5.read_Count END AS read_Count, "
				+ "A.first_name as From_Name, B.first_name as TO_name from messenger M1 "
						+" inner join user as A on A.id = M1.FROM_ID "
						+" inner join user as B on B.id = M1.TO_ID "
						+" left join (select conversation_id,count(M4.read_flag) as read_Count "
						+" from messenger M4 where M4.READ_FLAG = 0 group by CONVERSATION_ID) M5 "
						+" on M5.conversation_id = M1.conversation_id "	
						+" inner join( "
						+" select max(M2.date_time) as M2Date from messenger M2 group by CONVERSATION_ID) M3 "
						+ " on M1.date_time = M3.M2Date where "
						+ " M1.FROM_ID ="+id+" or M1.TO_ID ="+id+" order by id desc";
		List<MessagesById> messages = jdbcTemplate.query(query, new BeanPropertyRowMapper(MessagesById.class));
		return messages;
	}
	
	public List<ReadCount> getReadCount(int fromId){
		String query = "select messenger.to_id, count(messenger.READ_FLAG) as read_count from messenger " 
						+" where READ_FLAG = 0 and FROM_ID = "+fromId+" group by CONVERSATION_ID order by id desc";
		List<ReadCount> readCount = jdbcTemplate.query(query, new BeanPropertyRowMapper(ReadCount.class));
		return readCount;
	}
	
	public void updateRead(int conversationId, int toId){
		String query = "update messenger set READ_FLAG = 1 where conversation_id = "+conversationId+" and TO_ID ="+toId;
		this.jdbcTemplate.update(query);
	}
}
