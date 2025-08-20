package org.example.absolutecinema.controller.user;

import lombok.RequiredArgsConstructor;
import org.example.absolutecinema.dto.user.IdAndUserStatusDto;
import org.example.absolutecinema.dto.user.InfoForAdminDto;
import org.example.absolutecinema.entity.UserStatus;
import org.example.absolutecinema.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/users")
public class AdminUserRestController {
    private final UserService userService;

    @GetMapping
    public List<InfoForAdminDto> getAllUsers() {
        return userService.getInfoForAdmin();
    }

    @PatchMapping("/{id}/ban")
    public InfoForAdminDto banUser(@PathVariable Long id) {
        return userService.banUnbanUser(new IdAndUserStatusDto(id, UserStatus.BANNED));
    }

    @PatchMapping("/{id}/unban")
    public InfoForAdminDto unbanUser(@PathVariable Long id) {
        return userService.banUnbanUser(new IdAndUserStatusDto(id, UserStatus.ACTIVE));
    }
}
