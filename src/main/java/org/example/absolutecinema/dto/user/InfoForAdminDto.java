package org.example.absolutecinema.dto.user;

import org.example.absolutecinema.entity.UserStatus;

public record InfoForAdminDto(Long id,
                              String username,
                              String name,
                              UserStatus status) {
}
