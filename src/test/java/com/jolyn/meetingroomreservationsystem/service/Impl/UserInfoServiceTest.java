package com.jolyn.meetingroomreservationsystem.service.Impl;

import com.jolyn.meetingroomreservationsystem.domain.UserInfo;
import com.jolyn.meetingroomreservationsystem.domain.dto.ChangePasswordDto;
import com.jolyn.meetingroomreservationsystem.domain.dto.UserCreateDto;
import com.jolyn.meetingroomreservationsystem.domain.dto.UserInfoDto;
import com.jolyn.meetingroomreservationsystem.exception.EmailExistException;
import com.jolyn.meetingroomreservationsystem.exception.PasswordIncorrectException;
import com.jolyn.meetingroomreservationsystem.exception.UnableToSaveUserException;
import com.jolyn.meetingroomreservationsystem.exception.UserNotFoundException;
import com.jolyn.meetingroomreservationsystem.repository.UserInfoRepository;
import com.jolyn.meetingroomreservationsystem.security.JwtTokenProvider;
import com.jolyn.meetingroomreservationsystem.service.EmailService;
import jakarta.mail.SendFailedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class UserInfoServiceTest {
    @Mock
    private UserInfoRepository userInfoRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private UserInfoServiceImpl userInfoService;

    private UserCreateDto userCreateDto;

    private UserInfo testUser;
    private UserInfo testSavedUser;
    private UserInfo testUpdateUser;
    private UserInfoDto testUserDto;
    private UserCreateDto testCreateUserDto;
    private List<UserInfo> testUsers;
    private ChangePasswordDto testChangePasswordDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userCreateDto = new UserCreateDto();
        userCreateDto.setEmail("test@test.com");
        userCreateDto.setFirstName("Test");
        userCreateDto.setLastName("User");
        userCreateDto.setContact("123456");
        userCreateDto.setRole("ROLE_USER");

        testUser = new UserInfo();
        testUser.setId("1");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("johndoe@example.com");
        testUser.setContact("12345678");
        testUser.setPassword("password");
        testUser.setRole("ROLE_ADMIN");

        testSavedUser = new UserInfo();
        testSavedUser.setId("1");
        testSavedUser.setFirstName("Test");
        testSavedUser.setLastName("User");
        testSavedUser.setEmail("test@test.com");
        testSavedUser.setContact("123456");
        testSavedUser.setPassword("encoded_password");
        testSavedUser.setRole("ROLE_USER");

        testUpdateUser = new UserInfo();
        testUpdateUser.setId("1");
        testUpdateUser.setFirstName("John");
        testUpdateUser.setLastName("Doe");
        testUpdateUser.setEmail("johndoe@example.com");
        testUpdateUser.setContact("12333");
        testUpdateUser.setPassword("password");
        testUpdateUser.setRole("ROLE_ADMIN");

        testUserDto = new UserInfoDto(testUser.getId(), testUser.getFirstName(), testUser.getLastName(), testUser.getEmail(), testUser.getContact(), testUser.getRole());

        testUsers = new ArrayList<>();
        testUsers.add(testUser);

        testChangePasswordDto = new ChangePasswordDto();
        testChangePasswordDto.setId("1");
        testChangePasswordDto.setCurrentPassword("password");
        testChangePasswordDto.setNewPassword("newPassword");
    }

    @Test
    public void getAllUserInfoTest() {
        when(userInfoRepository.findAll()).thenReturn(testUsers);

        List<UserInfoDto> dtos = userInfoService.getAllUserInfo();

        assertEquals(1, dtos.size());
        assertEquals("1", dtos.get(0).getId());
        assertEquals("John", dtos.get(0).getFirstName());
        assertEquals("Doe", dtos.get(0).getLastName());
        assertEquals("johndoe@example.com", dtos.get(0).getEmail());
        assertEquals("12345678", dtos.get(0).getContact());
        assertEquals("ROLE_ADMIN", dtos.get(0).getRole());

        verify(userInfoRepository, times(1)).findAll();
    }

    @Test
    public void findByIdTest() throws UserNotFoundException {
        when(userInfoRepository.existsById("1")).thenReturn(true);
        when(userInfoRepository.findById("1")).thenReturn(Optional.ofNullable(testUser));

        UserInfo result = userInfoService.findById("1");

        assertEquals(testUser, result);

        verify(userInfoRepository, times(1)).existsById("1");
        verify(userInfoRepository, times(1)).findById("1");
    }

    @Test
    public void createUserTest() throws SendFailedException, EmailExistException, UnableToSaveUserException {
        when(userInfoRepository.existsByEmail(userCreateDto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encoded_password");
        when(userInfoRepository.save(any(UserInfo.class))).thenReturn(testSavedUser);
        doNothing().when(emailService).sendMail(any(String.class), any(String.class), any(String.class));

        UserInfo result = userInfoService.createUser(userCreateDto);

        assertEquals(testSavedUser, result);
        verify(userInfoRepository).existsByEmail(userCreateDto.getEmail());
        verify(passwordEncoder).encode(any());
        verify(userInfoRepository).save(any(UserInfo.class));
        verify(emailService).sendMail(any(String.class), any(String.class), any(String.class));
    }

    @Test
    public void createUserTest_EmailAlreadyExist() {
        when(userInfoRepository.existsByEmail(userCreateDto.getEmail())).thenReturn(true);
        assertThrows(EmailExistException.class, () -> userInfoService.createUser(userCreateDto));
    }

    @Test
    public void createUserTest_SaveUserFail() {
        when(userInfoRepository.existsByEmail(userCreateDto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encrypted_password");
        when(userInfoRepository.save(any(UserInfo.class))).thenThrow(new RuntimeException("Unable to save user"));
        assertThrows(UnableToSaveUserException.class, () -> userInfoService.createUser(userCreateDto));
    }

    @Test
    public void updateUserTest() throws Exception {
        when(userInfoRepository.findByEmail(testUser.getEmail())).thenReturn(testUser);
        when(userInfoRepository.save(testUpdateUser)).thenReturn(testUpdateUser);

        UserInfo result = userInfoService.updateUser(testUpdateUser);

        assertEquals(testUpdateUser, result);
        verify(userInfoRepository).findByEmail(testUpdateUser.getEmail());
        verify(userInfoRepository).save(testUpdateUser);
    }

    @Test
    public void deleteUserTest() {
        when(userInfoRepository.existsById(testUser.getId())).thenReturn(true);
        userInfoService.deleteUser(testUser.getId());
        verify(userInfoRepository).deleteById(testUser.getId());
    }

    @Test
    public void deleteUserTest_UserNotFound() {
        when(userInfoRepository.existsById("123")).thenReturn(false);
        assertThrows(UsernameNotFoundException.class, () -> userInfoService.deleteUser("123"));
    }

    @Test
    public void forgetPasswordTest() throws SendFailedException {
        when(userInfoRepository.findByEmail(testUser.getEmail())).thenReturn(testUser);
        when(passwordEncoder.encode(any())).thenReturn("encoded_password");
        testUser.setPassword("encoded_password");
        when(userInfoRepository.save(any(UserInfo.class))).thenReturn(testUser);
        doNothing().when(emailService).sendMail(any(String.class), any(String.class), any(String.class));

        userInfoService.forgetPassword(testUser.getEmail());

        verify(userInfoRepository).save(any(UserInfo.class));
        verify(emailService).sendMail(any(String.class), any(String.class), any(String.class));
    }

    @Test
    public void changePasswordTest() throws PasswordIncorrectException {
        when(userInfoRepository.findById("1")).thenReturn(Optional.ofNullable(testUser));
        when(passwordEncoder.matches("password", testUser.getPassword())).thenReturn(true);

        userInfoService.changePassword(testChangePasswordDto);
        verify(userInfoRepository).findById("1");
        verify(passwordEncoder).encode(any());
        verify(userInfoRepository).save(any(UserInfo.class));
    }

    @Test()
    public void testChangePassword_PasswordIncorrectException() throws PasswordIncorrectException {
        when(userInfoRepository.findById("1")).thenReturn(Optional.ofNullable(testUser));
        when(passwordEncoder.matches("password", testUser.getPassword())).thenReturn(false);

        assertThrows(PasswordIncorrectException.class, () -> userInfoService.changePassword(testChangePasswordDto));
    }

    @Test()
    public void testChangePassword_UsernameNotFoundException() throws PasswordIncorrectException {
        when(userInfoRepository.findById("1")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userInfoService.changePassword(testChangePasswordDto));
    }

}
