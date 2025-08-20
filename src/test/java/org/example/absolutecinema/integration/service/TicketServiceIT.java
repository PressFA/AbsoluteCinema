package org.example.absolutecinema.integration.service;

import org.example.absolutecinema.dto.ticket.CreateTicketDto;
import org.example.absolutecinema.integration.IT.IT;
import org.example.absolutecinema.service.TicketService;
import org.example.absolutecinema.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

@IT
public class TicketServiceIT {
    @Autowired
    private TicketService ticketService;
    @Autowired
    private UserService userService;

    @Test
    void testCreateTicket() {
        Long userId = 2L;
        CreateTicketDto ticketDto = new CreateTicketDto(6L, 2L);

        System.out.println(userService.depositBalance(userId, BigDecimal.valueOf(500)));

        System.out.println(ticketService.create(userId, ticketDto));
    }
}
