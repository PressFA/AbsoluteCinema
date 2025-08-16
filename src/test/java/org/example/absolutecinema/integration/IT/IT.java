package org.example.absolutecinema.integration.IT;

import org.example.absolutecinema.AbsoluteCinemaApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Transactional
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = AbsoluteCinemaApplication.class)
public @interface IT {
}
