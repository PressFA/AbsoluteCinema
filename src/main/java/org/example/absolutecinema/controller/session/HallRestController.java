package org.example.absolutecinema.controller.session;

import lombok.RequiredArgsConstructor;
import org.example.absolutecinema.dto.hall.FullInfoHallDto;
import org.example.absolutecinema.service.HallService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/halls")
public class HallRestController {
    private final HallService hallService;

    @GetMapping
    public List<FullInfoHallDto> getAllHalls() {
        return hallService.fetchAllHalls();
    }
}
