package Controller;

import io.javalin.Javalin;
import io.javalin.http.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;

/**
 * This class implements the social media controller with all required endpoints.
 */
public class SocialMediaController {
    private AccountService accountService;
    private MessageService messageService;
    private ObjectMapper mapper;

    public SocialMediaController() {
        this.accountService = new AccountService();
        this.messageService = new MessageService();
        this.mapper = new ObjectMapper();
    }

    /**
     * This method sets up all the endpoints for the social media application.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        
        // Account endpoints
        app.post("/register", this::registerHandler);
        app.post("/login", this::loginHandler);
        
        // Message endpoints
        app.post("/messages", this::createMessageHandler);
        app.get("/messages", this::getAllMessagesHandler);
        app.get("/messages/{message_id}", this::getMessageByIdHandler);
        app.delete("/messages/{message_id}", this::deleteMessageHandler);
        app.patch("/messages/{message_id}", this::updateMessageHandler);
        app.get("/accounts/{account_id}/messages", this::getMessagesByUserHandler);

        return app;
    }

    private void registerHandler(Context ctx) {
        try {
            Account account = mapper.readValue(ctx.body(), Account.class);
            Account registeredAccount = accountService.registerAccount(account);
            if (registeredAccount != null) {
                ctx.json(registeredAccount);
            } else {
                ctx.status(400);
            }
        } catch (JsonProcessingException e) {
            ctx.status(400);
        }
    }

    private void loginHandler(Context ctx) {
        try {
            Account credentials = mapper.readValue(ctx.body(), Account.class);
            Account loggedInAccount = accountService.login(credentials.getUsername(), credentials.getPassword());
            if (loggedInAccount != null) {
                ctx.json(loggedInAccount);
            } else {
                ctx.status(401);
            }
        } catch (JsonProcessingException e) {
            ctx.status(401);
        }
    }

    private void createMessageHandler(Context ctx) {
        try {
            Message message = mapper.readValue(ctx.body(), Message.class);
            Message createdMessage = messageService.createMessage(message);
            if (createdMessage != null) {
                ctx.json(createdMessage);
            } else {
                ctx.status(400);
            }
        } catch (JsonProcessingException e) {
            ctx.status(400);
        }
    }

    private void getAllMessagesHandler(Context ctx) {
        ctx.json(messageService.getAllMessages());
    }

    private void getMessageByIdHandler(Context ctx) {
        int messageId = Integer.parseInt(ctx.pathParam("message_id"));
        Message message = messageService.getMessageById(messageId);
        if (message != null) {
            ctx.json(message);
        } else {
            ctx.json("");
        }
    }

    private void deleteMessageHandler(Context ctx) {
        int messageId = Integer.parseInt(ctx.pathParam("message_id"));
        Message deletedMessage = messageService.deleteMessage(messageId);
        if (deletedMessage != null) {
            ctx.json(deletedMessage);
        } else {
            ctx.json("");
        }
    }

    private void updateMessageHandler(Context ctx) {
        try {
            int messageId = Integer.parseInt(ctx.pathParam("message_id"));
            Message messageUpdate = mapper.readValue(ctx.body(), Message.class);
            Message updatedMessage = messageService.updateMessage(messageId, messageUpdate.getMessage_text());
            if (updatedMessage != null) {
                ctx.json(updatedMessage);
            } else {
                ctx.status(400);
            }
        } catch (JsonProcessingException e) {
            ctx.status(400);
        }
    }

    private void getMessagesByUserHandler(Context ctx) {
        int accountId = Integer.parseInt(ctx.pathParam("account_id"));
        ctx.json(messageService.getMessagesByUser(accountId));
    }
}