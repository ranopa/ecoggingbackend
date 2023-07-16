package com.pickupluck.ecogging.domain.notification.service;

import com.pickupluck.ecogging.domain.notification.dto.NotificationResponseDto;
import com.pickupluck.ecogging.domain.notification.dto.NotificationSaveDto;
import com.pickupluck.ecogging.domain.notification.entity.Notification;
import com.pickupluck.ecogging.domain.notification.repository.NotificationRepository;
import com.pickupluck.ecogging.domain.user.entity.User;
import com.pickupluck.ecogging.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final UserRepository userRepository;

    private final NotificationRepository notificationRepository;

    private NotificationResponseDto notificationToResponse(Notification notification) {
        return NotificationResponseDto.builder()
                .targetId(notification.getTargetId())
                .senderId(notification.getSender().getId())
                .senderNickname(notification.getSender().getNickname())
                .typeName(notification.getType().getName())
                .detail(notification.getDetail())
                .createdAt(notification.getCreatedAt())
                .build();
    }

    public List<NotificationResponseDto> getMyNotifications(Long receiverId) {
        List<Notification> notifications = notificationRepository.findByReceiverId(receiverId);

        return notifications.stream()
                .map(notification -> notificationToResponse(notification))
                .toList();
    }

    public void createNotification(NotificationSaveDto notificationSaveDto) {
        User sender = userRepository.findById(notificationSaveDto.getSenderId())
                .orElseThrow(() -> new InvalidParameterException("No User(Sender) for Given Id"));
        User receiver = userRepository.findById(notificationSaveDto.getReceiverId())
                .orElseThrow(() -> new InvalidParameterException("No User(Receiver) for Given Id"));


        Notification notification = Notification.builder()
                                        .sender(sender)
                                        .receiver(receiver)
                                        .type(notificationSaveDto.getType())
                                        .targetId(notificationSaveDto.getTargetId())
                                        .detail(notificationSaveDto.getDetail())
                                        .isRead(false)
                                        .isDeleted(false)
                                        .build();

        notificationRepository.save(notification);
    }

    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }

}