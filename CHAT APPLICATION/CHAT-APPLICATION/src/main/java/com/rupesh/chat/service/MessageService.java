package com.rupesh.chat.service;

import com.rupesh.chat.entity.Conversation;
import com.rupesh.chat.entity.Message;
import com.rupesh.chat.entity.UnreadMessageCount;
import com.rupesh.chat.model.*;
import com.rupesh.chat.repository.ConversationRepository;
import com.rupesh.chat.repository.MessageRepository;
import com.rupesh.chat.repository.UnreadMessageCountRepository;
import com.rupesh.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.springframework.http.RequestEntity.put;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final UnreadMessageCountRepository countRepository;

    public MessageResponse sendMessage(MessageRequest request, String sender) {
        Conversation conversation = conversationRepository.findById(request.getConversationId())
                .orElseThrow(() -> new RuntimeException("No conversation found for conversation " + request.getConversationId()));

        Message savedMessage = messageRepository.save(
                Message.builder()
                        .content(request.getContent())
                        .type(request.getType())
                        .sender(sender)
                        .isDeleted(false)
                        .conversation(conversation)
                        .status(MessageStatus.SENT)
                        .build()
        );

        conversation.setLastMessage(savedMessage);
        conversationRepository.save(conversation);

        //UPDATE MESSAGE COUNT
        if (savedMessage.getStatus() == MessageStatus.SENT || savedMessage.getStatus() == MessageStatus.DELIVERY) {

            var receivers = conversation.getParticipants()
                    .stream()
                    .filter(participant ->
                            !participant.getUsername().equals(savedMessage.getSender())
                                    && !participant.getEmail().equalsIgnoreCase(savedMessage.getSender())
                    )
                    .collect(Collectors.toSet());

            for (User receiver : receivers) {
                Optional<UnreadMessageCount> byUsernameAndConversationIdOpt = countRepository.findByUsernameAndConversationId(receiver.getUsername(), conversation.getId());
                if (byUsernameAndConversationIdOpt.isPresent()) {
                    countRepository.incrementUnreadCount(1, receiver.getUsername(), conversation.getId());

                    messagingTemplate.convertAndSendToUser(unreadMessageCountTopic(String.valueOf(conversation.getId()), receiver.getEmail()), "/queue/messages",
                            UnreadMessageCountResponse
                                    .builder()
                                    .conversationId(conversation.getId())
                                    .username(receiver.getUsername())
                                    .count(byUsernameAndConversationIdOpt.get().getUnreadCount() + 1)
                                    .build());
                } else {
                    countRepository.save(UnreadMessageCount
                            .builder()
                            .conversationId(conversation.getId())
                            .username(receiver.getUsername())
                            .unreadCount(1)
                            .build());

                    messagingTemplate.convertAndSendToUser(unreadMessageCountTopic(String.valueOf(conversation.getId()), receiver.getEmail()), "/queue/messages",
                            UnreadMessageCountResponse
                                    .builder()
                                    .conversationId(conversation.getId())
                                    .username(receiver.getUsername())
                                    .count(1)
                                    .build());
                }

                messagingTemplate.convertAndSendToUser(receiver.getUsername() + "global.unreadCount", "/queue/messages", conversationRepository.unreadMessageCount(receiver.getUsername()));
            }

        }

        return MessageResponse
                .builder()
                .conversationId(conversation.getId())
                .id(savedMessage.getId())
                .status(savedMessage.getStatus())
                .sender(
                        conversation.getParticipants()
                                .parallelStream()
                                .filter(conv ->
                                        conv.getUsername().equals(savedMessage.getSender())
                                                || conv.getEmail().equalsIgnoreCase(savedMessage.getSender())
                                )
                                .findFirst()
                                .map(participant ->
                                        ParticipantResponse
                                                .builder()
                                                .name(capitalize(participant.getFirstName()) + " " + capitalize(participant.getLastName()))
                                                .username(participant.getUsername())
                                                .email(participant.getEmail())
                                                .profile(participant.getProfile())
                                                .isOnline(participant.isOnline())
                                                .lastSeen(!participant.isOnline() ? participant.getLastLogout() : null)
                                                .role("USER")
                                                .unreadCount(0)

                                                .build()
                                )
                                .get()
                )
                .content(savedMessage.getContent())
                .createdOn(savedMessage.getCreatedOn())
                .createdBy(savedMessage.getCreatedBy())
                .modifiedOn(savedMessage.getModifiedOn())
                .modifiedBy(savedMessage.getModifiedBy())
                .build();

    }

    //only by receiver not sender
    @Transactional
    public void markMessageAsRead(Long conversationId, User receiver) {
        List<MessageStatusProjection> projections = messageRepository.getMessagesToUpdateStatus(conversationId, receiver.getUsername());

        Set<String> users = new HashSet<>();
        if (projections.size() > 0) {
            for (MessageStatusProjection projection : projections) {
                messageRepository.updateMessageStatus(projection.getMessageId(), MessageStatus.READ.name());
                users.add(projection.getSender());
            }

            for (String user : users)
                messagingTemplate.convertAndSendToUser(
                        user, "/queue/messages",
                        projections
                );

            Optional<UnreadMessageCount> byUsernameAndConversationIdOpt = countRepository.findByUsernameAndConversationId(receiver.getUsername(), conversationId);
            if (byUsernameAndConversationIdOpt.isPresent()) {
                //update and set unread message count to 0
                var unreadMessageCount = byUsernameAndConversationIdOpt.get();
                unreadMessageCount.setUnreadCount(0);
                countRepository.save(unreadMessageCount);
            }

            messagingTemplate.convertAndSendToUser(unreadMessageCountTopic(String.valueOf(conversationId), receiver.getEmail()), "/queue/messages",
                    UnreadMessageCountResponse
                            .builder()
                            .conversationId(conversationId)
                            .username(receiver.getUsername())
                            .count(0)
                            .build());

        }

        messagingTemplate.convertAndSendToUser(receiver.getUsername() + "global.unreadCount", "/queue/messages", conversationRepository.unreadMessageCount(receiver.getUsername()));
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }

    private static String unreadMessageCountTopic(String conversationId, String receiver) {
        return format("%s%s", conversationId, receiver);
    }

}