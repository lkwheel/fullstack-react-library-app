package com.wheelerkode.library.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
@Getter
@NoArgsConstructor
public class OutstandingFeesException extends RuntimeException {

    public OutstandingFeesException(String message) {
        super(message);
    }

    public OutstandingFeesException(String message, Throwable cause) {
        super(message, cause);
    }

    public OutstandingFeesException(Throwable cause) {
        super(cause);
    }
}
