package DAO;

import Controller.InvalidAccountIDException;
import Controller.InvalidMessageIDException;
import Model.Account;
import Model.Message;
import Service.UsernameTakenException;
import Util.ConnectionUtil;
import javax.security.auth.login.CredentialNotFoundException;
import java.sql.*;
import java.util.HashSet;

public class ServiceDAO {

    private static Connection conn;

    public ServiceDAO() {
        conn = ConnectionUtil.getConnection();
    }

    private boolean userAlreadyExists(String username) {
        String sql = "SELECT account_id FROM account WHERE username = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet results = stmt.executeQuery();
            return results.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Account getAccountByCredentials(String username, String password) throws CredentialNotFoundException {
        String sql = "SELECT * FROM account WHERE username = ? AND password = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet results = stmt.executeQuery();
            if (results.next()) {
                Account account = new Account(results.getInt(1), results.getString(2), results.getString(3));
                return account;
            } else throw new CredentialNotFoundException();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Account createNewUser(String username, String password) throws UsernameTakenException {
        if (userAlreadyExists(username)) throw new UsernameTakenException();
        String sql = "INSERT INTO account (username, password) VALUES (?, ?);";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            int results = stmt.executeUpdate();
            stmt.getGeneratedKeys().next();
            if (results == 1) {
                Account createdAccount = new Account(stmt.getGeneratedKeys().getInt(1), username, password);
                return createdAccount;
            } else throw new SQLException();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public HashSet<Message> getAllMessages() {
        String sql = "SELECT * FROM message;";
        HashSet<Message> messageList = new HashSet<>();
        try (Statement stmt = conn.createStatement()) {
            ResultSet results = stmt.executeQuery(sql);
            while (results.next()) {
                int messageId = results.getInt(1);
                int postedBy = results.getInt(2);
                String messageText = results.getString(3);
                long timePostedEpoch = results.getLong(4);
                Message nextMessage = new Message(messageId, postedBy, messageText, timePostedEpoch);
                messageList.add(nextMessage);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return messageList;
    }

    public Message createMessage(Message newMessage) {
        if (newMessage.getMessage_text() == null || newMessage.getMessage_text().trim().isEmpty() || 
            newMessage.getMessage_text().length() > 255) {
            throw new RuntimeException("Invalid message text");
        }
        String sql = "INSERT INTO message (posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?);";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, newMessage.getPosted_by());
            stmt.setString(2, newMessage.getMessage_text());
            stmt.setLong(3, newMessage.getTime_posted_epoch());
            if (stmt.executeUpdate() == 1) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                generatedKeys.next();
                return new Message(generatedKeys.getInt(1), newMessage.getPosted_by(), newMessage.getMessage_text(), newMessage.getTime_posted_epoch());
            } else throw new SQLException();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Message updateMessage(int messageId, String messageText) throws InvalidMessageIDException {
        getMessageByMessageId(messageId);
        String sql;
        int postedBy;
        long timePostedEpoch;
        sql = "UPDATE message SET message_text = ? WHERE message_id = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, messageText);
            stmt.setInt(2, messageId);
            int didItWork = stmt.executeUpdate();
            if (didItWork != 1) throw new SQLException();
            sql = "SELECT * FROM message WHERE message_id = ?;";
            try (PreparedStatement retrieve = conn.prepareStatement(sql)) {
                retrieve.setInt(1, messageId);
                ResultSet result = retrieve.executeQuery();
                result.next();
                postedBy = result.getInt(2);
                timePostedEpoch = result.getLong(4);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new Message(messageId, postedBy, messageText, timePostedEpoch);
    }

    public Message getMessageByMessageId(int messageId) throws InvalidMessageIDException {
        String sql = "SELECT * FROM message WHERE message_id = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, messageId);
            ResultSet results = stmt.executeQuery();
            if (results.next()) {
                int postedBy = results.getInt(2);
                String messageText = results.getString(3);
                long timePostedEpoch = results.getLong(4);
                return new Message(messageId, postedBy, messageText, timePostedEpoch);
            } else {
                throw new InvalidMessageIDException();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Message deleteMessage(int messageId) throws InvalidMessageIDException {
        Message deletedMessage = getMessageByMessageId(messageId);
        String sql = "DELETE FROM message WHERE message_id=?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, messageId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return deletedMessage;
    }

    public Account getAccountByID(int accountId) throws InvalidAccountIDException {
        String sql = "SELECT * FROM account WHERE account_id = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            ResultSet results = stmt.executeQuery();
            if (results.next()) {
                String username = results.getString(2);
                String password = results.getString(3);
                return new Account(accountId, username, password);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        throw new InvalidAccountIDException();
    }

    public HashSet<Message> getAllAccountsMessages(int accountId) {
        HashSet<Message> messageSet = new HashSet<>();
        String sql = "SELECT * FROM message WHERE posted_by = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            ResultSet results = stmt.executeQuery();
            while (results.next()) {
                Message nextMessage = new Message(results.getInt(1), results.getInt(2), results.getString(3), results.getLong(4));
                messageSet.add(nextMessage);
            }
            return messageSet;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}


