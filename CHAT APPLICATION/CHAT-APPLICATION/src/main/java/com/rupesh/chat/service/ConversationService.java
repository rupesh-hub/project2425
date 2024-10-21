package com.rupesh.chat.service;

import com.rupesh.chat.entity.Conversation;
import com.rupesh.chat.entity.UnreadMessageCount;
import com.rupesh.chat.model.*;
import com.rupesh.chat.repository.ConversationRepository;
import com.rupesh.chat.repository.UnreadMessageCountRepository;
import com.rupesh.shared.GlobalResponse;
import com.rupesh.user.entity.User;
import com.rupesh.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final UnreadMessageCountRepository countRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public GlobalResponse<List<ConversationProjection>> getConversations(User creator) {
        List<ConversationProjection> conversationsForUser = conversationRepository.findConversationsForUser(creator.getUsername());
        for (ConversationProjection conversationProjection : conversationsForUser) {

            var conversationId = conversationProjection.getId();
            Optional<UnreadMessageCount> byUsernameAndConversationIdOpt = countRepository.findByUsernameAndConversationId(creator.getUsername(), conversationId);
            UnreadMessageCountResponse response = UnreadMessageCountResponse
                    .builder()
                    .conversationId(conversationId)
                    .username(creator.getUsername())
                    .count(0)
                    .build();
            if (byUsernameAndConversationIdOpt.isPresent()) {
                response.setCount(byUsernameAndConversationIdOpt.get().getUnreadCount());
            }

            var topic = unreadMessageCountTopic(String.valueOf(conversationId), creator.getEmail());
            messagingTemplate.convertAndSendToUser(topic, "/queue/messages", response);

            messagingTemplate.convertAndSendToUser(creator.getUsername() + "global.unreadCount", "/queue/messages", conversationRepository.unreadMessageCount(creator.getUsername()));

        }
        return GlobalResponse.success(conversationsForUser);
    }


    public void createConversation(ConversationRequest request) {

        boolean isGroup = request.getType().name().equalsIgnoreCase("GROUP");

        String conversationName = isGroup ? request.getName() : "";
        String avatar = isGroup ? request.getAvatar() : "";

        // check conversation
        Conversation conversation = Conversation.builder()
                .name(conversationName)
                .avatar(avatar)
                .type(request.getType())
                .participants(
                        Arrays.stream(request.getParticipants())
                                .map(participant -> userRepository.findBy(participant)
                                        .orElseThrow(() -> new RuntimeException("Could not find user by username."))
                                )
                                .collect(Collectors.toSet())
                )
                .build();
        conversationRepository.save(conversation);
    }

    public GlobalResponse<ConversationResponse> getConversationById(Long conversationId, String authUser) {
        var conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("No conversation found for conversation id " + conversationId));
        return GlobalResponse.success(convert(conversation, authUser));
    }

    public GlobalResponse<List<ConversationProjection>> search(int page, int limit, String query, String creator) {
        return GlobalResponse.success(conversationRepository.searchConversations(creator, query, PageRequest.of(page, limit)));
    }

    private ConversationResponse convert(Conversation conversation, String authUser) {

        boolean isGroup = conversation.getType().name().equalsIgnoreCase("GROUP");
        String conversationName = conversation.getName();
        String conversationAvatar = conversation.getAvatar();

        // if isGroup false
        // we have to get participant that is not equals to loggedInUser
        boolean isOnline = true;
        LocalDateTime lastSeen = null;

        if (!isGroup) {
            var participantFirst = conversation.getParticipants()
                    .stream()
                    .filter(conv ->
                            !conv.getUsername().equals(authUser) && !conv.getEmail().equalsIgnoreCase(authUser)
                    )
                    .findFirst()
                    .get();

            conversationName = capitalize(participantFirst.getFirstName()) + " " + capitalize(participantFirst.getLastName());
            conversationAvatar = participantFirst.getProfile();
            isOnline = participantFirst.isOnline();
            lastSeen = participantFirst.getLastLogout();
        }
        var lastMessage = conversation.getLastMessage();
        return ConversationResponse
                .builder()
                .id(conversation.getId())
                .name(conversationName)
                .avatar(conversationAvatar)
                .isGroup(isGroup)
                .createdBy(conversation.getCreatedBy())
                .createdOn(conversation.getCreatedOn())
                .updatedBy(conversation.getModifiedBy())
                .updatedOn(conversation.getModifiedOn())
                .isOnline(isOnline)
                .lastSeen(lastSeen)
                .lastMessageId(lastMessage.getId())
                .lastMessageContent(lastMessage.getContent())
                .lastMessageSentTime(lastMessage.getCreatedOn())
                .messageStatus(lastMessage.getStatus())
                .lastMessageSender(lastMessage.getSender())
                .participants(conversation.getParticipants()
                        .stream()
                        .map(participant -> ParticipantResponse
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
                        .collect(Collectors.toSet()))
                .messages(
                        conversation.getMessages()
                                .stream()
                                .map(message -> MessageResponse
                                        .builder()
                                        .id(message.getId())
                                        .content(message.getContent())
                                        .status(message.getStatus())
                                        .createdBy(message.getCreatedBy())
                                        .createdOn(message.getCreatedOn())
                                        .modifiedBy(message.getModifiedBy())
                                        .modifiedOn(message.getModifiedOn())
                                        .sender(
                                                conversation.getParticipants()
                                                        .parallelStream()
                                                        .filter(conv ->
                                                                conv.getUsername().equals(message.getSender())
                                                                        || conv.getEmail().equalsIgnoreCase(message.getSender())
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
                                        .build())
                                .collect(Collectors.toList()
                                ))
                .build();
    }

    public void addParticipant(String username, Long conversationId) {
        var conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("No conversation found for conversation id " + conversationId));

        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("No user found for user " + username));

        conversation.getParticipants().add(user);

        conversationRepository.save(conversation);
    }

    public void deleteConversation(Long conversationId, String creator) {

        // query into db for conversation
        Conversation conversation = conversationRepository.getByConversationIdAndParticipant(conversationId, creator)
                .orElseThrow(() -> new RuntimeException("No conversation found for conversation id " + conversationId));

        // check if conversation is created by logged-in user
        if (!conversation.getCreatedBy().equals(creator)) {
            throw new RuntimeException("User is not authorized to delete this conversation.");
        }

        // remove conversation from db
        conversationRepository.deleteById(conversationId);

        // remove all unread messages from conversation
        // remove all messages from conversation
        // remove user from participants of conversation
        // decrement unread count for all participants
        // notify all participants about the deletion
        // delete all notifications related to conversation
        // delete all attachments related to conversation
        // delete all comments related to conversation
        // delete all likes related to conversation
        // delete all dislikes related to conversation
        // delete all bookmarks related to conversation
        // delete all events related to conversation
        // delete all polls related to conversation
        // delete all ratings related to conversation
        // delete all reviews related to conversation
        // delete all recommendations related to conversation
        // delete all shares related to conversation
        // delete all shared links related to conversation
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
