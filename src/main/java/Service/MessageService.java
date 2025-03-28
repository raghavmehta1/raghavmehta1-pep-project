package com.revature.service;

import com.revature.dao.AccountDAO;
import com.revature.dao.MessageDAO;
import com.revature.model.Message;
import java.util.List;

public class MessageService {
    private MessageDAO messageDAO;
    private AccountDAO accountDAO;
    
    public MessageService() {
        messageDAO = new MessageDAO();
        accountDAO = new AccountDAO();
    }
    
    public Message createMessage(Message message) {
        // Validation: message_text not blank and under 255 characters
        if (message.getMessage_text() == null || 
            message.getMessage_text().trim().isEmpty() || 
            message.getMessage_text().length() > 255) {
            return null;
        }
        
        // Validation: posted_by refers to a real user
        if (accountDAO.getAccountById(message.getPosted_by()) == null) {
            return null;
        }
        
        return messageDAO.createMessage(message);
    }
    
    public List<Message> getAllMessages() {
        return messageDAO.getAllMessages();
    }
    
    public Message getMessageById(int messageId) {
        return messageDAO.getMessageById(messageId);
    }
    
    public Message deleteMessage(int messageId) {
        Message message = messageDAO.getMessageById(messageId);
        if (message != null) {
            messageDAO.deleteMessage(messageId);
        }
        return message;
    }
    
    public Message updateMessage(int messageId, String messageText) {
        // Validation: message exists
        Message existingMessage = messageDAO.getMessageById(messageId);
        if (existingMessage == null) {
            return null;
        }
        
        // Validation: message_text not blank and under 255 characters
        if (messageText == null || 
            messageText.trim().isEmpty() || 
            messageText.length() > 255) {
            return null;
        }
        
        return messageDAO.updateMessage(messageId, messageText);
    }
    
    public List<Message> getMessagesByUser(int accountId) {
        return messageDAO.getMessagesByUser(accountId);
    }
}
