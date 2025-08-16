package org.example.absolutecinema.dto.auth;

import org.example.absolutecinema.entity.Role;

public record JwtPayloadDto(Long id,
                            String username,
                            Role role) {
}
