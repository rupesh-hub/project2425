package com.rupesh.chat.repository;

import com.rupesh.chat.entity.Message;
import com.rupesh.chat.model.MessageStatusProjection;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT M FROM Message M WHERE M.conversation.id = :conversationId")
    List<Message> allMessages(@Param("conversationId") Long conversationId);


    @Query(value = """
                SELECT distinct m.id as messageId, m.sender as sender, c.id as conversationId, 'READ' as status
                FROM _conversation c
                                INNER JOIN _conversation_participants cp ON c.id = cp.conversation_id
                                INNER JOIN _message m ON c.id = m.conversation_id
                                WHERE c.id = :conversationId AND m.sender != :auth AND m.status != 'READ'
            """,
            nativeQuery = true)
    List<MessageStatusProjection> getMessagesToUpdateStatus(@Param("conversationId") Long conversationId, @Param("auth") String auth);

    @Modifying
    @Transactional
    @Query(value = "UPDATE _message SET status = :status WHERE id = :messageId", nativeQuery = true)
    int updateMessageStatus(@Param("messageId") Long messageId, @Param("status") String status);

}
