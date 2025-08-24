package org.example.absolutecinema.config.scheduler;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
// Включает поддержку планировщика задач
@EnableScheduling
// Разрешает выполнение задач асинхронно; бех этого метода не будет работать аннотация @Async
@EnableAsync
// Позволяет включать или отключать бин в зависимости от свойства scheduler.enabled
// Если в тестах установить scheduler.enabled=false, планировщик не будет стартовать
@ConditionalOnProperty(name = "scheduler.enabled", matchIfMissing = true)
public class SchedulerConfiguration {
}
