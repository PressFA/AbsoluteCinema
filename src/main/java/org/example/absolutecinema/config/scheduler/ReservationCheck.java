package org.example.absolutecinema.config.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.absolutecinema.dto.scheduler.TicketAndUserDto;
import org.example.absolutecinema.service.TicketService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationCheck {
    private final TicketService ticketService;

    // Говорит Spring запускать его в отдельном потоке
    // Если предыдущий запуск ещё не завершён, Spring создаст новый поток для следующего вызова
    @Async
    // Превращает метод в задачу, которая запускается по расписанию
    // Конкретная частота или момент запуска задаются параметрами аннотации
    @Scheduled(fixedRate = 600000)
    public void task() {
        log.info("Запуск задачи ReservationCheck: проверка просроченных бронирований");
        List<TicketAndUserDto> tickets = ticketService.fetchTicketsByStatusAndExpiresAt();
        if (!tickets.isEmpty()) {
            tickets.forEach(ticketService::taskExecution);
        }
    }
}
