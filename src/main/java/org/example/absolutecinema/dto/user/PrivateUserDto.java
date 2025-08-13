package org.example.absolutecinema.dto.user;

import lombok.Builder;

@Builder
public record PrivateUserDto(String username,
                             String password) {
}
