package org.example.absolutecinema.dto.seat;

import org.example.absolutecinema.entity.Status;

public record RespInfoSeatDto(Long id,
                              String row,
                              int place,
                              Status status) {
}
