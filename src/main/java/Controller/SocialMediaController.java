package com.revature.controller;

import com.revature.model.Account;
import com.revature.model.Message;
import com.revature.service.AccountService;
import com.revature.service.MessageService;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class SocialMediaController {
    private AccountService accountService;
    private MessageService messageService;

    public SocialMediaController() {
        accountService = new AccountService();
        messageService = new MessageService();
    }

    public Javalin startAPI() {
        Javalin app = Javalin.create();
        
        // User Registration
        app.post("/register", this::registerUser);
        
        // User Login
        app.post("/login", this::loginUser);
        
        // Create Message
        app.post("/messages", this::createMessage);
        
        // Get All Messages
        app.get("/messages", this::getAllMessages);
        
        // Get Message by ID
        app.get("/messages/{message_id}", this::getMessageById);
        
        // Delete Message
        app.delete("/messages/{message_id}", this::deleteMessage);
        
        // Update Message
        app.patch("/messages/{message_id}", this::updateMessage);
        
        // Get User Messages
        app.get("/accounts/{account_id}/messages", this::getUserMessages);
        
        return app;
    }
    
    private void registerUser(Context ctx) {
        Account account = ctx.bodyAsClass(Account.class);
        Account registeredAccount = accountService.registerAccount(account);
        
        if (registeredAccount != null) {
            ctx.json(registeredAccount);
        } else {
            ctx.status(400);
        }
    }
    
    private void loginUser(Context ctx) {
        Account account = ctx.bodyAsClass(Account.class);
        Account loggedInAccount = accountService.loginAccount(account);
        
        if (loggedInAccount != null) {
            ctx.json(loggedInAccount);
        } else {
            ctx.status(401);
        }
    }
    
    private void createMessage(Context ctx) {
        Message message = ctx.bodyAsClass(Message.class);
        Message createdMessage = messageService.createMessage(message);
        
        if (createdMessage != null) {
            ctx.json(createdMessage);
        } else {
            ctx.status(400);
        }
    }
    
    private void getAllMessages(Context ctx) {
        ctx.json(messageService.getAllMessages());
    }
    
    private void getMessageById(Context ctx) {
        int messageId = Integer.parseInt(ctx.pathParam("message_id"));
        Message message = messageService.getMessageById(messageId);
        ctx.json(message != null ? message : "");
    }
    
    private void deleteMessage(Context ctx) {
        int messageId = Integer.parseInt(ctx.pathParam("message_id"));
        Message deletedMessage = messageService.deleteMessage(messageId);
        ctx.json(deletedMessage != null ? deletedMessage : "");
    }
    
    private void updateMessage(Context ctx) {
        int messageId = Integer.parseInt(ctx.pathParam("message_id"));
        Message messageUpdate = ctx.bodyAsClass(Message.class);
        
        Message updatedMessage = messageService.updateMessage(messageId, messageUpdate.getMessage_text());
        
        if (updatedMessage != null) {
            ctx.json(updatedMessage);
        } else {
            ctx.status(400);
        }
    }
    
    private void getUserMessages(Context ctx) {
        int accountId = Integer.parseInt(ctx.pathParam("account_id"));
        ctx.json(messageService.getMessagesByUser(accountId));
    }
}
