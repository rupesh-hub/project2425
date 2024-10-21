package com.rupesh.chat.resource;

import com.rupesh.chat.model.*;
import com.rupesh.chat.service.ConversationService;
import com.rupesh.chat.service.MessageService;
import com.rupesh.chat.service.NotificationService;
import com.rupesh.shared.GlobalResponse;
import com.rupesh.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/conversations")
@RequiredArgsConstructor
public class ConversationResource {

    private final ConversationService conversationService;
    private final MessageService messageService;
    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;

    //fetch chats
    @GetMapping("/conversation.all")
    public ResponseEntity<GlobalResponse<List<ConversationProjection>>> allConversations(Authentication authentication) {
        return ResponseEntity.ok(conversationService.getConversations((User)authentication.getPrincipal()));
    }

    @GetMapping("/conversation.byId")
    public ResponseEntity<GlobalResponse<ConversationResponse>> conversationById(@RequestParam("conversationId") Long conversationId, Authentication authentication) {
        return ResponseEntity.ok(conversationService.getConversationById(conversationId, authentication.getName()));
    }

    //add chat
    @PostMapping("/conversation.create")
    public ResponseEntity<Void> createConversation(@RequestBody ConversationRequest request) {
        conversationService.createConversation(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/conversation.delete")
    public ResponseEntity<Void> deleteConversation(@RequestParam Long conversationId, Authentication authentication) {
        conversationService.deleteConversation(conversationId, authentication.getName());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/participant.add")
    public ResponseEntity<Void> addParticipant(@RequestParam("username") String username,
                                               @RequestParam("conversationId") Long conversationId) {
        conversationService.addParticipant(username, conversationId);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    //create conversation
    @PostMapping("/message.send")
    public ResponseEntity<MessageResponse> sendMessage(@RequestBody MessageRequest request, Authentication authentication) {
        MessageResponse response = messageService.sendMessage(request, authentication.getName());
        String receiver = String.valueOf(request.getConversationId());
        messagingTemplate.convertAndSendToUser(receiver, "/queue/messages", response);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/notification.save")
    public ResponseEntity<Void> saveNotification(@RequestBody NotificationRequest request) {
        notificationService.save(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/notification.all")
    public ResponseEntity<GlobalResponse<List<NotificationResponse>>> getAllNotifications(Authentication authentication) {
        return ResponseEntity.ok(notificationService.getNotifications((User) authentication.getPrincipal()));
    }

    // mark message as read
    @PutMapping("/mark.asRead")
    public ResponseEntity<Void> markMessageAsRead(@RequestParam("conversationId") Long conversationId, Authentication authentication) {
        messageService.markMessageAsRead(conversationId, (User)authentication.getPrincipal());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/search")
    public ResponseEntity<GlobalResponse<List<ConversationProjection>>> searchConversations(@Param("query") String query,
                                                                                            @Param("page") int page,
                                                                                            @Param("limit") int limit,
                                                                                            Authentication authentication) {
        return ResponseEntity.ok(conversationService.search(page, limit, query, authentication.getName()));
    }

}