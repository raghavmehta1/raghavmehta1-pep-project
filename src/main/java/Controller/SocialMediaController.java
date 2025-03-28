package Controller;

import Model.Account;
import Model.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;
import Service.*;
import javax.security.auth.login.CredentialNotFoundException;

public class SocialMediaController {

    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.post("/register", this::registerNewUser);
        app.post("/login", this::login);
        app.post("/messages", this::createMessage);
        app.get("/messages", this::getAllMessages);
        app.get("/messages/{message_id}", this::getMessage);
        app.delete("/messages/{message_id}", this::deleteMessage);
        app.patch("/messages/{message_id}", this::updateMessage);
        app.get("/accounts/{account_id}/messages", this::getUsersMessages);

        return app;
    }

    private static final ObjectMapper om = new ObjectMapper();
    private final Service svc = new Service();

    private void registerNewUser(Context ctx) throws JsonProcessingException {
        Account newAccount = om.readValue(ctx.body(), Account.class);
        try {
            newAccount = svc.registerNewUser(newAccount);
            String resultString = om.writeValueAsString(newAccount);
            ctx.result(resultString);

        } catch (InvalidUsernameException | UnacceptablePasswordException | UsernameTakenException e) {
            ctx.result("").status(400);
        }
    }

    private void login(Context ctx) throws JsonProcessingException {
        Account userAccount = om.readValue(ctx.body(), Account.class);
        try {
            userAccount = svc.userLogin(userAccount.getUsername(), userAccount.getPassword());
            String body = om.writeValueAsString(userAccount);
            ctx.result(body);
        } catch (CredentialNotFoundException e) {
            ctx.result("").status(401);
        }
    }

    private void createMessage(Context ctx) throws JsonProcessingException {
        Message newMessage = om.readValue(ctx.body(), Message.class);                   // Does not handle malformed JSON. Will return 500 (should be 400)
        try {
            newMessage = svc.createNewMessage(newMessage);
            String resultString = om.writeValueAsString(newMessage);
            ctx.result(resultString);
        } catch (MessageLengthOOBException | InvalidAccountIDException e) {
            ctx.status(400);
        }
    }

    private void getAllMessages(Context ctx) throws JsonProcessingException {
        String resultString = om.writeValueAsString(svc.getAllMessages());
        ctx.result(resultString);
    }

    private void updateMessage(Context ctx) throws JsonProcessingException {
        JsonNode root = om.readTree(ctx.body());
        String messageText = root.get("message_text").asText();

        if (messageText == null || messageText.trim().isEmpty() || messageText.length() > 255) {
            ctx.result("").status(400);
            return;
        }

        try {
            int message_id = Integer.parseInt(ctx.pathParam("message_id"));
            Message updatedMessage = svc.updateMessage(message_id, messageText);
            String resultString = om.writeValueAsString(updatedMessage);
            ctx.result(resultString);
        } catch (InvalidMessageIDException e) {
            ctx.result("").status(400);
        } catch (NumberFormatException e) {
            ctx.result("").status(400);
        }
    }

    private void getMessage(Context ctx) throws JsonProcessingException {
        try {
            int messageId = Integer.parseInt(ctx.pathParam("message_id"));
            Message retrievedMessage = svc.getMessageByMessageID(messageId);
            String resultString = om.writeValueAsString(retrievedMessage);
            ctx.result(resultString);
        } catch (InvalidMessageIDException e) {
            ctx.result("");
        } catch (NumberFormatException e) {
            ctx.result("");
        }
    }

    private void deleteMessage(Context ctx) throws JsonProcessingException {
        int messageId = Integer.parseInt(ctx.pathParam("message_id"));
        Message deletedMessage;
        String resultString;
        try {
            deletedMessage = svc.deleteMessage(messageId);
            resultString = om.writeValueAsString(deletedMessage);
        } catch (InvalidMessageIDException e) {
            resultString = "";
        }
        ctx.result(resultString);
    }

    private void getUsersMessages(Context ctx) throws JsonProcessingException {
        int accountId = Integer.parseInt(ctx.pathParam("account_id"));
        String resultString;
        try {
            resultString = om.writeValueAsString(svc.getAllAccountsMessages(accountId));
            ctx.result(resultString);
        } catch (InvalidAccountIDException e) {
            ctx.result("");
        }
    }

}