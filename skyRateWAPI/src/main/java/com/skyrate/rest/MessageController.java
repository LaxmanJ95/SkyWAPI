package com.skyrate.rest;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.skyrate.clib.model.SuccessIDResponse;
import com.skyrate.clib.service.email.EmailService;
import com.skyrate.clib.service.email.EmailTemplateService;
import com.skyrate.clib.util.JSONUtil;
import com.skyrate.model.dbentity.ConversationMapping;
import com.skyrate.model.dbentity.Messenger;
import com.skyrate.model.dbentity.User;
import com.skyrate.model.email.EmailMessage;
import com.skyrate.model.message.ConversationRequest;
import com.skyrate.model.message.InboxRequest;
import com.skyrate.model.message.MailRecipient;
import com.skyrate.model.message.MailToClientRequest;
import com.skyrate.model.message.MessageListRequest;
import com.skyrate.model.message.MessageListResponse;
import com.skyrate.model.message.MessageRequest;
import com.skyrate.model.message.MessagesById;
import com.skyrate.model.message.ReadCount;
import com.skyrate.model.message.ReadCountRequest;
import com.skyrate.model.message.ReadCountResponse;
import com.skyrate.service.MessageService;
import com.skyrate.service.UserMgmtService;

@RestController
@RequestMapping("/message")
@CrossOrigin(maxAge = 3600)
public class MessageController {

	private static final Logger logger =  LoggerFactory.getLogger(MessageController.class);
	
	@Autowired
	MessageService messageService;
	@Autowired
	private EmailService emailService;
	@Autowired
	private UserMgmtService userMgmtService;
	@Autowired
	private EmailTemplateService emailTemplateService;
	
	@RequestMapping(method = RequestMethod.POST, value ="/addMessage")
	public SuccessIDResponse addMessage(@RequestBody MessageRequest request){
		SuccessIDResponse response = new SuccessIDResponse();
		try{
			System.out.println("request "+JSONUtil.toJson(request));
			ConversationMapping conversationMapping = messageService.conversationMapping(request.getFrom(), request.getTo());
			if(conversationMapping == null){
				conversationMapping = new ConversationMapping();
				conversationMapping.setMessenger1(request.getFrom());
				conversationMapping.setMessenger2(request.getTo());
				messageService.saveConversationMapping(conversationMapping);
				System.out.println("conv mapping "+JSONUtil.toJson(conversationMapping));
				logger.info("conversation id");
			}
			Messenger messenger = new Messenger();
			messenger.setFromId(request.getFrom());
			messenger.setToId(request.getTo());
			messenger.setMessage(request.getMessage());
			messenger.setConversationId(conversationMapping.getId());
			messenger.setDateTime(new Date());
			messenger.setReviewId(request.getReviewId());
			System.out.println("messenger "+JSONUtil.toJson(messenger));
			messageService.saveMessenger(messenger);
			logger.info("message added");
		}
		catch(Exception e){
			logger.error("message failed",e);
			response.setSuccess(false);
		}
		return response;
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/getMessagesById")
	public MessageListResponse getMessagesById(@RequestBody MessageListRequest request){
		MessageListResponse response = new MessageListResponse();
		try{
			System.out.println("request "+JSONUtil.toJson(request));
			ConversationMapping conversationMapping = messageService.conversationMapping(request.getFromId(), request.getToId());
//			messageService.updateRead(conversationMapping.getId(), request.getToId());
			List<MessagesById> messages = messageService.getMessagesById(conversationMapping.getId());
			response.setMessages(messages);
			logger.info("message list");
		}
		catch(Exception e){
			logger.error("message list failed",e);
			response.setSuccess(false);
		}
		return response;
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/getInboxById")
	public MessageListResponse getInboxById(@RequestBody InboxRequest request){
		MessageListResponse response = new MessageListResponse();
		try{
			List<MessagesById> messages = messageService.getInbox(request.getId());
			response.setMessages(messages);
			logger.info("inbox list");
		}
		catch(Exception e){
			logger.error("inbox list failed",e);
			response.setSuccess(false);
		}
		return response;
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/getByConversationId")
	public MessageListResponse getByConversationId(@RequestBody ConversationRequest request){
		MessageListResponse response = new MessageListResponse();
		try{
			messageService.updateRead(request.getId(), request.getToId());
			List<MessagesById> messages = messageService.getMessagesById(request.getId());
			response.setMessages(messages);
			logger.info("message list");
		}
		catch(Exception e){
			logger.error("message list failed",e);
			response.setSuccess(false);
		}
		return response;
	}
	
	@RequestMapping(method = RequestMethod.POST, value ="/getReadCount")
	public ReadCountResponse getReadCount(@RequestBody ReadCountRequest request){
		ReadCountResponse response = new ReadCountResponse();
		try{
			List<ReadCount> readCount = messageService.getReadCount(request.getFromId());
			response.setReadCount(readCount);
			logger.info("readCount list");
		}
		catch(Exception e){
			logger.error("count list failed",e);
			response.setSuccess(false);
		}
		return response;
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/mailToClient")
	public SuccessIDResponse mailToClient(@RequestBody MailToClientRequest request){
		SuccessIDResponse response = new SuccessIDResponse();
		try{
			final String SEPARATOR = ",";
			List<MailRecipient> mailRecipient = null;
			User user = userMgmtService.getUserById(request.getUserMail());
			StringBuilder csvBuilder = new StringBuilder();
			for(MailRecipient mail : request.getMailRecipient()){
				csvBuilder.append(mail.getValue());
			    csvBuilder.append(SEPARATOR);
			}
			System.out.println("csv "+csvBuilder);
			String csv = csvBuilder.toString();
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("content", request.getBody());
			String subject = request.getSubject();
			String emailBody = emailTemplateService.getEmailTemplate("mailToClient.vm",map);
//			EmailMessage emailMessage = new EmailMessage();
//			emailMessage.setToEmail(user.getEmail());
//			emailMessage.setSubject(subject);
//			emailMessage.setEmailBody(emailBody);
			emailService.sendEmailToClients(user.getEmail(), subject, emailBody, csv);
		}
		catch(Exception e){
			logger.error("mail to client failed",e);
			response.setSuccess(false);
		}
		return response;
	}
}
