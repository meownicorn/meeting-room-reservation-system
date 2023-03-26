package com.jolyn.meetingroomreservationsystem.service.Impl;

import com.jolyn.meetingroomreservationsystem.constant.enumeration.Role;
import com.jolyn.meetingroomreservationsystem.domain.UserInfo;
import com.jolyn.meetingroomreservationsystem.domain.dto.LoginDto;
import com.jolyn.meetingroomreservationsystem.domain.dto.RegisterDto;
import com.jolyn.meetingroomreservationsystem.exception.EmailExistException;
import com.jolyn.meetingroomreservationsystem.exception.UnableToSaveUserException;
import com.jolyn.meetingroomreservationsystem.repository.UserInfoRepository;
import com.jolyn.meetingroomreservationsystem.security.JwtTokenProvider;
import com.jolyn.meetingroomreservationsystem.service.AuthService;
import com.jolyn.meetingroomreservationsystem.service.EmailService;
import jakarta.mail.SendFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.jolyn.meetingroomreservationsystem.constant.EmailConstant.REGISTER_MAIL_TEMPLATE;
import static com.jolyn.meetingroomreservationsystem.constant.SecurityConstant.JWT_TOKEN_HEADER;
import static com.jolyn.meetingroomreservationsystem.exception.ExceptionHandling.EMAIL_ALREADY_EXIST;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private AuthenticationManager manager;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private EmailService emailService;

    public static final String USER_IS_LOCKED = "User is locked. Please contact Administrator";

    @Override
    public UserInfo register(RegisterDto dto) throws EmailExistException, UnableToSaveUserException, SendFailedException {
        if (userInfoRepository.findByEmail(dto.getEmail()) == null) {
            UserInfo newUser = new UserInfo();
            newUser.setFirstName(dto.getFirstName());
            newUser.setLastName(dto.getLastName());
            newUser.setEmail(dto.getEmail());
            newUser.setContact(dto.getContact());
            newUser.setPassword(encoder.encode(dto.getPassword()));
            newUser.setRole(Role.ROLE_USER.name());

            UserInfo userInfo;
            try {
                userInfo = userInfoRepository.save(newUser);

                String templateData = "{\"name\":\"" + userInfo.getFirstName() + " " + userInfo.getLastName() + "\"}";
                emailService.sendMail(templateData, REGISTER_MAIL_TEMPLATE, userInfo.getEmail());
            } catch (Exception e) {
                throw new UnableToSaveUserException("Unable to save user: " + e.getMessage());
            }
            return userInfo;
        } else {
            throw new EmailExistException(EMAIL_ALREADY_EXIST);
        }
    }

    @Override
    public HttpHeaders login(LoginDto dto) {
        authenticate(dto.getEmail(), dto.getPassword());
        UserInfo loginUser = userInfoRepository.findByEmail(dto.getEmail());
        HttpHeaders jwtHeader = getJwtHeader(loginUser);
        return jwtHeader;
    }

    private HttpHeaders getJwtHeader(UserInfo loginUser) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(loginUser));
        return headers;
    }
    private void authenticate(String username, String password) {
        manager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }
}
