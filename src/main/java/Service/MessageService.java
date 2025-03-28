package Service;

import Model.Message;
import DAO.MessageDAO;
import DAO.AccountDAO;
import java.util.List;

public class MessageService {
    private MessageDAO messageDAO;
    private AccountDAO accountDAO;

    public MessageService() {
        this.messageDAO = new MessageDAO();
        this.accountDAO = new AccountDAO();
    }

    public Message createMessage(Message message) {
        if (message.getMessage_text() == null || 
            message.getMessage_text().trim().isEmpty() || 
            message.getMessage_text().length() > 255) {
            return null;
        }
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
        return messageDAO.deleteMessage(messageId);
    }

    public Message updateMessage(int messageId, String newMessageText) {
        if (newMessageText == null || 
            newMessageText.trim().isEmpty() || 
            newMessageText.length() > 255) {
            return null;
        }
        Message existingMessage = messageDAO.getMessageById(messageId);
        if (existingMessage == null) {
            return null;
        }
        return messageDAO.updateMessage(messageId, newMessageText);
    }

    public List<Message> getMessagesByUser(int accountId) {
        return messageDAO.getMessagesByUser(accountId);
    }
} 