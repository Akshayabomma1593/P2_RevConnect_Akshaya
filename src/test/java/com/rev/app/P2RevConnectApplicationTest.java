package com.rev.app;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import static org.junit.jupiter.api.Assertions.assertTrue;

class P2RevConnectApplicationTest {

    @Test
    void applicationClass_hasExpectedAnnotations() {
        assertTrue(P2RevConnectApplication.class.isAnnotationPresent(SpringBootApplication.class));
        assertTrue(P2RevConnectApplication.class.isAnnotationPresent(EnableScheduling.class));
    }
}
