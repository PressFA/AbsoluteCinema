package org.example.absolutecinema.repository;

import org.example.absolutecinema.dto.auth.JwtPayloadDto;
import org.example.absolutecinema.dto.user.InfoUserDto;
import org.example.absolutecinema.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    JwtPayloadDto findJwtPayloadByUsername(String username);

    InfoUserDto findInfoUserById(Long id);
}
