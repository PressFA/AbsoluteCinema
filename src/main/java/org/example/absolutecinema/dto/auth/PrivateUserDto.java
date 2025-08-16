package org.example.absolutecinema.dto.auth;

import lombok.Builder;

@Builder
public record PrivateUserDto(String username,
                             String password) {
}
