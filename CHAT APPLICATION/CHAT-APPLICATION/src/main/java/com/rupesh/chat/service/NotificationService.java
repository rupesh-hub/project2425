package com.rupesh.chat.service;

import com.rupesh.chat.entity.Notification;
import com.rupesh.chat.model.NotificationRequest;
import com.rupesh.chat.model.NotificationResponse;
import com.rupesh.chat.repository.NotificationRepository;
import com.rupesh.shared.GlobalResponse;
import com.rupesh.user.entity.User;
import com.rupesh.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public void save(NotificationRequest request) {
        var notification = Notification
                .builder()
                .recipient(request.getRecipient())
                .action(request.getAction())
                .actionBy(request.getActionBy())
                .timestamp(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
    }

    public GlobalResponse<List<NotificationResponse>> getNotifications(User user) {

        return GlobalResponse.success(
                Optional.ofNullable(notificationRepository.getNotifications(user.getUsername(), user.getEmail()))
                .orElse(Collections.emptyList())
                .stream()
                .map(notification -> {
                    // Fetch the user who performed the action (actionBy)
                    User actionByUser = userRepository.findByUsername(notification.getActionBy())
                            .orElseThrow(() -> new RuntimeException("User not found"));

                    return NotificationResponse.builder()
                            .actionByProfile(actionByUser.getProfile())
                            .actionBy(capitalize(actionByUser.getFirstName() )+ " " + capitalize(actionByUser.getLastName()))
                            .id(notification.getId())
                            .action(notification.getAction())
                            .recipient(notification.getRecipient())
                            .timestamp(notification.getTimestamp())
                            .build();
                })
                .collect(Collectors.toList())
        );
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }

}