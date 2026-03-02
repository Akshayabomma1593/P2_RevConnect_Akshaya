package com.rev.app.service;

import com.rev.app.entity.Message;
import com.rev.app.entity.User;
import com.rev.app.repository.MessageRepository;
import com.rev.app.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MessageService messageService;

    @Test
    void sendMessage_success() {
        User sender = new User();
        sender.setId(1L);
        User recipient = new User();
        recipient.setId(2L);
        when(userRepository.findById(2L)).thenReturn(Optional.of(recipient));
        when(messageRepository.save(any(Message.class))).thenAnswer(i -> i.getArgument(0));

        Message message = messageService.sendMessage(sender, 2L, "hello");

        assertEquals("hello", message.getContent());
    }

    @Test
    void sendMessage_missingRecipient_throws() {
        User sender = new User();
        sender.setId(1L);
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> messageService.sendMessage(sender, 99L, "hello"));
    }
}
