package com.naman.notification;

import com.naman.clients.notification.NotificationRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public void sendNotification(NotificationRequest request) {

        Notification notification = Notification.builder()
                .toCustomerId(request.toCustomerId())
                .toCustomerEmail(request.toCustomerName())
                .sender("TechieNaman")
                .message(request.message())
                .sentAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
    }

}
