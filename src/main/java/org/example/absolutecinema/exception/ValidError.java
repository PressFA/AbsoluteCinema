package org.example.absolutecinema.exception;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
@Slf4j
public class ValidError {
    private int status;
    private Map<String, String> message;
    private Date timestamp;

    public ValidError(int status, Map<String, String> message) {
        this.status = status;
        this.message = message;
        this.timestamp = new Date();
    }

    public static ResponseEntity<?> validationReq(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        bindingResult.getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        return new ResponseEntity<>(
                new ValidError(HttpStatus.BAD_REQUEST.value(), errors),
                HttpStatus.BAD_REQUEST
        );
    }
}
