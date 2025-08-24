package org.example.absolutecinema.utils.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.absolutecinema.entity.Status;

import java.util.List;

public class AllowedStatusValidator implements ConstraintValidator<AllowedStatus, Status> {
    private final List<Status> allowed = List.of(Status.RESERVED, Status.PURCHASED);

    @Override
    public boolean isValid(Status value, ConstraintValidatorContext context) {
        if (value == null) { return false; }
        return allowed.contains(value);
    }
}
