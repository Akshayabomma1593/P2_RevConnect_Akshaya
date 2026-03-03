package com.rev.app.exception;

import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleNotFound_sets404Model() {
        Model model = new ExtendedModelMap();

        String view = handler.handleNotFound(new ResourceNotFoundException("Not found"), model);

        assertEquals("error", view);
        assertEquals(404, model.getAttribute("errorCode"));
    }

    @Test
    void handleGeneral_sets500Model() {
        Model model = new ExtendedModelMap();

        String view = handler.handleGeneral(new RuntimeException("boom"), model);

        assertEquals("error", view);
        assertEquals(500, model.getAttribute("errorCode"));
    }
}
