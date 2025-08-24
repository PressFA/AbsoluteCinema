package org.example.absolutecinema.integration.service;

import lombok.RequiredArgsConstructor;
import org.example.absolutecinema.integration.IT.IT;
import org.example.absolutecinema.service.HallService;
import org.junit.jupiter.api.Test;

@IT
@RequiredArgsConstructor
public class HallServiceIT {
    private final HallService hallService;

    @Test
    void testFetchAllHalls() {
        var resp = hallService.fetchAllHalls();
        var body = resp.getBody();

        System.out.println(body);
    }
}
