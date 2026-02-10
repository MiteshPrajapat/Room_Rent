package com.roomRent.RoomRent.controller;

import com.roomRent.RoomRent.dto.RegisterRequest;
import com.roomRent.RoomRent.dto.UserResponse;
import com.roomRent.RoomRent.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserProfile(@PathVariable String id) {
        UserResponse userResponse = userService.getUserProfile(id);
        return ResponseEntity.ok(userResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest request) {
        UserResponse userResponse = userService.register(request);
        return ResponseEntity.ok(userResponse);
    }
}
