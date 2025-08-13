package org.example.absolutecinema.dto.user;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record InfoUserDto(Long id,
                          String name,
                          BigDecimal balance) {
}
