package org.example.absolutecinema.repository;

import org.example.absolutecinema.dto.auth.JwtPayloadDto;
import org.example.absolutecinema.dto.user.InfoForAdminDto;
import org.example.absolutecinema.dto.user.InfoUserDto;
import org.example.absolutecinema.entity.Role;
import org.example.absolutecinema.entity.User;
import org.example.absolutecinema.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    List<InfoForAdminDto> findByRoleNot(Role role);

    @Modifying
    @Query("""
    update User
    set status = :status
    where id = :id
    """)
    void updateStatus(@Param("id") Long userId, @Param("status")UserStatus status);

    InfoForAdminDto findInfoForAdminDtoById(Long id);

    Optional<User> findByUsername(String username);

    JwtPayloadDto findJwtPayloadByUsername(String username);

    InfoUserDto findInfoUserById(Long id);
}
