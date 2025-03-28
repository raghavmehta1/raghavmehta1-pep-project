package Service;

import Controller.InvalidAccountIDException;
import Controller.InvalidMessageIDException;
import Controller.MessageLengthOOBException;
import Model.Account;
import DAO.ServiceDAO;
import Model.Message;
import javax.security.auth.login.CredentialNotFoundException;
import java.util.HashSet;


public class Service {

    private static final ServiceDAO dao = new ServiceDAO();

    public Account registerNewUser(Account newAccount) throws InvalidUsernameException, UnacceptablePasswordException, UsernameTakenException {
        String username = newAccount.getUsername();
        String password = newAccount.getPassword();
        if (username.isEmpty() || username.length() > 255) throw new InvalidUsernameException();
        if (password.length() < 4 || password.length() > 255) throw new UnacceptablePasswordException();
        return dao.createNewUser(username, password);
    }

    public Account userLogin(String username, String password) throws CredentialNotFoundException {
        return dao.getAccountByCredentials(username, password);
    }

    public Message createNewMessage(Message newMessage) throws MessageLengthOOBException, InvalidAccountIDException {
        if (newMessage.getMessage_text().length()<1 || newMessage.getMessage_text().length()>255) throw new MessageLengthOOBException();
        dao.getAccountByID(newMessage.getPosted_by());  // This is to check that an account with that ID exists in db. DAO will throw exception for us if not
        return dao.createMessage(newMessage);
    }

    public HashSet<Message> getAllMessages() {
        return dao.getAllMessages();
    }

    public Message updateMessage(int message_id, String message_text) throws InvalidMessageIDException {
        return dao.updateMessage(message_id, message_text);
    }

    public Message getMessageByMessageID(int messageId) throws InvalidMessageIDException {
        return dao.getMessageByMessageId(messageId);
    }

    public Message deleteMessage(int messageId) throws InvalidMessageIDException {
        return dao.deleteMessage(messageId);
    }

    public HashSet<Message> getAllAccountsMessages(int accountId) throws InvalidAccountIDException {
        return dao.getAllAccountsMessages(accountId);
    }
}