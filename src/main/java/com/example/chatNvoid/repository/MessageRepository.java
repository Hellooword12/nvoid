package com.example.chatNvoid.repository;

import com.example.chatNvoid.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long>
{

    long countBySenderUsername(String username);
    Optional<Message> findTopByReceiverIsNullOrderByTimestampDesc();

    // Сообщения общего чата (receiver = null)
    List<Message> findByReceiverIsNullOrderByTimestampAsc();

        @Query("SELECT m FROM Message m JOIN FETCH m.sender " +
                "WHERE (m.sender.id = :userId1 AND m.receiver.id = :userId2) " +
                "OR (m.sender.id = :userId2 AND m.receiver.id = :userId1) " +
                "ORDER BY m.timestamp ASC")
        List<Message> findMessagesBetweenUsers(@Param("userId1") Long userId1,
                                               @Param("userId2") Long userId2);


    @Query("SELECT m FROM Message m " +
            "JOIN FETCH m.sender s " +
            "LEFT JOIN FETCH m.receiver r " +
            "WHERE (m.sender.id = :userId OR m.receiver.id = :userId) " +
            "ORDER BY m.timestamp DESC")
    List<Message> findMessagesWithSenders(@Param("userId") Long userId);
}
