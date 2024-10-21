package com.rupesh.chat.repository;

import com.rupesh.chat.entity.Conversation;
import com.rupesh.chat.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    @Query(nativeQuery = true, value = """
                WITH user_info AS (SELECT id
                                                                                 FROM _users
                                                                                 WHERE username = :usernameOrEmail
                                                                                    OR LOWER(email) = LOWER(:usernameOrEmail))
                                                              SELECT co.id,
                                                                     co.type as conversationType,
                                                                     m.content    AS lastMessageContent,
                                                                     m.created_on AS lastMessageSentTime,
                                                                     m.status AS messageStatus,
                                                                      m.id as lastMessageId,
                                                                      m.sender as lastMessageSender,
                                                                     CASE
                                                                         WHEN co.type = 'GROUP' THEN co.name
                                                                         ELSE INITCAP(CONCAT(u2.first_name, ' ', u2.last_name))
                                                                         END      AS name,
                                                              
                                                                     CASE
                                                                         WHEN co.type = 'GROUP' THEN co.avatar
                                                                         ELSE u2.profile
                                                                         END      AS avatar,
                                                              
                                                                  CASE
                                                                         WHEN co.type = 'INDIVIDUAL' THEN u2.is_online
                                                                         ELSE true
                                                                         END      AS isOnline
                                                              FROM _conversation co
                                                                       JOIN _conversation_participants cp ON cp.conversation_id = co.id
                                                                       JOIN user_info ui ON cp.participant_id = ui.id
                                                                       LEFT JOIN _message m ON m.id = co.last_message_id
                                                                       LEFT JOIN _conversation_participants cp2 ON cp2.conversation_id = co.id AND cp2.participant_id != ui.id
                                                                       LEFT JOIN _users u2 ON u2.id = cp2.participant_id
                                                              WHERE co.id IN (SELECT conversation_id
                                                                              FROM _conversation_participants
                                                                              WHERE participant_id = ui.id)
            """)
    List<ConversationProjection> findConversationsForUser(@Param("usernameOrEmail") String usernameOrEmail);

    @Query(value = "SELECT C FROM Conversation C WHERE C.createdBy = :username")
    List<Conversation> getConversations(@Param("username") String username);

    @Query(value = "SELECT C FROM Conversation C WHERE C.id = :conversationId AND C.createdBy = :username")
    Optional<Conversation> getByConversationIdAndParticipant(@Param("conversationId") Long conversationId, @Param("username") String username);

    @Query(nativeQuery = true, value = """
            WITH user_info AS (SELECT id
                                       FROM _users
                                       WHERE username = :username
                                          OR LOWER(email) = LOWER(:username))
                    SELECT co.id,
                           co.type as conversationType,
                           m.content    AS lastMessageContent,
                           m.created_on AS lastMessageSentTime,
                           m.status AS messageStatus,
                           m.id as lastMessageId,
                           m.sender as lastMessageSender,
                           CASE
                               WHEN co.type = 'GROUP' THEN co.name
                               ELSE INITCAP(CONCAT(u2.first_name, ' ', u2.last_name))
                               END      AS name,
                           CASE
                               WHEN co.type = 'GROUP' THEN co.avatar
                               ELSE u2.profile
                               END      AS avatar,
                           CASE
                               WHEN co.type = 'INDIVIDUAL' THEN u2.is_online
                               ELSE true
                               END      AS isOnline
                    FROM _conversation co
                             JOIN _conversation_participants cp ON cp.conversation_id = co.id
                             JOIN user_info ui ON cp.participant_id = ui.id
                             LEFT JOIN _message m ON m.id = co.last_message_id
                             LEFT JOIN _conversation_participants cp2 ON cp2.conversation_id = co.id AND cp2.participant_id != ui.id
                             LEFT JOIN _users u2 ON u2.id = cp2.participant_id
                    WHERE co.id IN (SELECT conversation_id
                                    FROM _conversation_participants
                                    WHERE participant_id = ui.id)
                      AND (LOWER(co.name) LIKE LOWER(CONCAT('%', :query , '%'))
                           OR LOWER(CONCAT(u2.first_name, ' ', u2.last_name)) LIKE LOWER(CONCAT('%', :query , '%')))
            """)
    List<ConversationProjection> searchConversations(@Param("username") String username, @Param("query") String query, Pageable pageable);

    @Query(nativeQuery = true, value = "select sum(unread_count) from unread_message_count where username = :username")
    int unreadMessageCount(@Param("username") String username);

}
