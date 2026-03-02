package com.rev.app.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EntityPackageTest {

    @Test
    void constructorsAndDefaults_work() {
        User sender = new User("alice", "alice@example.com", "pw");
        User receiver = new User("bob", "bob@example.com", "pw");

        Connection connection = new Connection(sender, receiver);
        assertEquals(Connection.Status.PENDING, connection.getStatus());
        assertEquals(sender, connection.getSender());
        assertEquals(receiver, connection.getReceiver());

        Follow follow = new Follow(sender, receiver);
        assertEquals(sender, follow.getFollower());
        assertEquals(receiver, follow.getFollowed());

        Post post = new Post(sender, "hello");
        Comment comment = new Comment(post, receiver, "nice");
        Message message = new Message(sender, receiver, "hi");
        Notification notification = new Notification(receiver, sender, Notification.NotificationType.NEW_FOLLOWER, "followed");

        assertEquals("hello", post.getContent());
        assertEquals("nice", comment.getContent());
        assertEquals("hi", message.getContent());
        assertEquals(Notification.NotificationType.NEW_FOLLOWER, notification.getType());
    }
}
