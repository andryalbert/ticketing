package com.demo.ticketing.utils.exception;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;

@Builder
public record ErrorResponse(
        int statusCode,
        ErrorType errorType,
        Map<String, String> details,
        String suggestion,
        LocalDateTime timestamp) {

}

