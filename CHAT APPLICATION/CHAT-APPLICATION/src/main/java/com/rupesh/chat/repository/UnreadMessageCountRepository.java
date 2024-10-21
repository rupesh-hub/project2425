package com.rupesh.chat.repository;

import com.rupesh.chat.entity.UnreadMessageCount;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UnreadMessageCountRepository extends  JpaRepository<UnreadMessageCount, Long>  {

    @Query(value = "SELECT * FROM unread_message_count C WHERE C.username=:username AND C.conversation_id=:conversationId",
    nativeQuery = true)
    Optional<UnreadMessageCount> findByUsernameAndConversationId(String username, Long conversationId);

    @Modifying
    @Transactional
    @Query("UPDATE UnreadMessageCount c SET c.unreadCount = c.unreadCount + :increment WHERE c.username = :username AND c.conversationId = :conversationId")
    int incrementUnreadCount(@Param("increment") int increment, @Param("username") String username, @Param("conversationId") Long conversationId);


}
