package org.example.absolutecinema.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Year;

public class MaxCurrentYearValidator implements ConstraintValidator<MaxCurrentYear, Integer> {
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) { return false; }
        int currentYear = Year.now().getValue();
        return value <= currentYear;
    }
}
