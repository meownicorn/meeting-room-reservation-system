package com.jolyn.meetingroomreservationsystem.service.Impl;

import com.jolyn.meetingroomreservationsystem.domain.UserInfo;
import com.jolyn.meetingroomreservationsystem.domain.dto.LoginDto;
import com.jolyn.meetingroomreservationsystem.domain.dto.RegisterDto;
import com.jolyn.meetingroomreservationsystem.exception.EmailExistException;
import com.jolyn.meetingroomreservationsystem.exception.UnableToSaveUserException;
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
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class AuthServiceTest {
    @Mock
    private UserInfoRepository userInfoRepository;
    @Mock
    private PasswordEncoder encoder;
    @Mock
    private AuthenticationManager manager;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private EmailService emailService;
    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterDto testRegisterDto;
    private UserInfo testUser;
    private LoginDto testLoginDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testRegisterDto = new RegisterDto();
        testRegisterDto.setEmail("test@test.com");
        testRegisterDto.setContact("12345");
        testRegisterDto.setFirstName("John");
        testRegisterDto.setLastName("Doe");
        testRegisterDto.setPassword("password");

        testLoginDto = new LoginDto();
        testLoginDto.setEmail("test@test.com");
        testLoginDto.setPassword("password");

        testUser = new UserInfo();
        testUser.setId("1");
        testUser.setEmail("test@test.com");
        testUser.setContact("12345");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setPassword("password");
        testUser.setRole("ROLE_USER");
    }

    @Test
    public void registerTest() throws SendFailedException, EmailExistException, UnableToSaveUserException {
        when(userInfoRepository.findByEmail(any())).thenReturn(null);
        when(encoder.encode(any())).thenReturn("encoded_password");
        when(userInfoRepository.save(any(UserInfo.class))).thenReturn(testUser);
        doNothing().when(emailService).sendMail(any(String.class), any(String.class), any(String.class));

        UserInfo result = authService.register(testRegisterDto);

        assertNotNull(result);
        assertEquals(testUser, result);
        verify(userInfoRepository).findByEmail(any());
        verify(encoder).encode(any());
        verify(userInfoRepository).save(any((UserInfo.class)));
        verify(emailService).sendMail(any(String.class), any(String.class), any(String.class));
    }

    @Test
    public void registerTest_EmailExistException() {
        when(userInfoRepository.findByEmail(any())).thenReturn(testUser);
        assertThrows(EmailExistException.class, () -> authService.register(testRegisterDto));
    }

    @Test
    public void registerTest_SaveUserFail() {
        when(userInfoRepository.findByEmail(any())).thenReturn(null);
        when(encoder.encode(any())).thenReturn("encrypted_password");
        when(userInfoRepository.save(any(UserInfo.class))).thenThrow(new RuntimeException("Unable to save user"));
        assertThrows(UnableToSaveUserException.class, () -> authService.register(testRegisterDto));

    }

    @Test
    public void loginTest() {
        when(userInfoRepository.findByEmail(testUser.getEmail())).thenReturn(testUser);
        when(jwtTokenProvider.generateJwtToken(any(UserInfo.class))).thenReturn("test_token");

        HttpHeaders headers = authService.login(testLoginDto);

        assertNotNull(headers);
    }
}
