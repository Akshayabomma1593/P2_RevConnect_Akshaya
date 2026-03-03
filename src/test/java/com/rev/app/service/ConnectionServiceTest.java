package com.rev.app.service;

import com.rev.app.entity.Connection;
import com.rev.app.entity.User;
import com.rev.app.repository.ConnectionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConnectionServiceTest {

    @Mock
    private ConnectionRepository connectionRepository;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ConnectionService connectionService;

    @Test
    void sendRequest_success() {
        User sender = new User();
        sender.setId(1L);
        sender.setUsername("alice");
        User receiver = new User();
        receiver.setId(2L);
        receiver.setUsername("bob");
        Connection connection = new Connection(sender, receiver);

        when(connectionRepository.connectionExists(1L, 2L)).thenReturn(false);
        when(connectionRepository.save(any(Connection.class))).thenReturn(connection);

        Connection saved = connectionService.sendRequest(sender, receiver);

        assertEquals(sender, saved.getSender());
        assertEquals(receiver, saved.getReceiver());
    }

    @Test
    void sendRequest_existingConnection_throws() {
        User sender = new User();
        sender.setId(1L);
        User receiver = new User();
        receiver.setId(2L);
        when(connectionRepository.connectionExists(1L, 2L)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> connectionService.sendRequest(sender, receiver));
    }
}
