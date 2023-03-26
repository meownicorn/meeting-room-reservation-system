package com.jolyn.meetingroomreservationsystem.controller;

import com.jolyn.meetingroomreservationsystem.domain.UserInfo;
import com.jolyn.meetingroomreservationsystem.domain.dto.ChangePasswordDto;
import com.jolyn.meetingroomreservationsystem.domain.dto.UserCreateDto;
import com.jolyn.meetingroomreservationsystem.domain.dto.UserInfoDto;
import com.jolyn.meetingroomreservationsystem.exception.*;
import com.jolyn.meetingroomreservationsystem.service.UserInfoService;
import jakarta.mail.SendFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserInfoController extends ExceptionHandling {
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private AuthenticationManager manager;

    Logger log = LoggerFactory.getLogger(getClass());

    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<UserInfo> createUser(@RequestBody UserCreateDto dto) throws UnableToSaveUserException, SendFailedException, EmailExistException {
        log.info("Create New User");
        return ResponseEntity.ok(userInfoService.createUser(dto));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<UserInfoDto>> findAllUsers() {
        log.info("Get All Users");
        return ResponseEntity.ok(userInfoService.getAllUserInfo());

    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<UserInfo> updateUser(@RequestBody UserInfo userInfo) throws EmailExistException {
        log.info("Update User ID: " + userInfo.getId());
        return ResponseEntity.ok(userInfoService.updateUser(userInfo));
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        log.info("Delete User ID: " + id);
        userInfoService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserInfo> getUserById(@PathVariable String id) throws UserNotFoundException {
        log.info("Find User ID: " + id);
        return ResponseEntity.ok(userInfoService.findById(id));
    }

    @PostMapping("/change-password")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDto dto) throws PasswordIncorrectException {
        log.info("Change Password");
        userInfoService.changePassword(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/forget-password")
    public ResponseEntity<?> forgetPassword(@RequestParam String email) throws SendFailedException {
        log.info("Forget Password for Email: " + email);
        userInfoService.forgetPassword(email);
        return ResponseEntity.ok().build();
    }
}
