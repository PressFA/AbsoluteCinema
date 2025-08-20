package org.example.absolutecinema.dto.user;

import org.example.absolutecinema.entity.UserStatus;

public record IdAndUserStatusDto(Long id,
                                 UserStatus status) {
}
