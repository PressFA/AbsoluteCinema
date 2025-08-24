package org.example.absolutecinema.utils.annotation;

import jakarta.validation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AllowedStatusValidator.class)
public @interface AllowedStatus {
    String message() default "Недопустимый статус билета";
    Class<?>[] groups() default {};
    Class<?>[] payload() default {};
}
