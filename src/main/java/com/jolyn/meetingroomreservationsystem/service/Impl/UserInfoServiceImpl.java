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
import com.jolyn.meetingroomreservationsystem.service.UserInfoService;
import jakarta.mail.SendFailedException;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.jolyn.meetingroomreservationsystem.constant.EmailConstant.FORGET_PASSWORD_TEMPLATE;
import static com.jolyn.meetingroomreservationsystem.constant.EmailConstant.NEW_CREATE_USER_TEMPLATE;
import static com.jolyn.meetingroomreservationsystem.exception.ExceptionHandling.*;

@Service
@Transactional
public class UserInfoServiceImpl implements UserInfoService {
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private EmailService emailService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public List<UserInfoDto> getAllUserInfo() {
        List<UserInfo> users = userInfoRepository.findAll();
        List<UserInfoDto> dtos = users.stream()
                .map(u -> new UserInfoDto(u.getId(), u.getFirstName(), u.getLastName(), u.getEmail(), u.getContact(), u.getRole()))
                .collect(Collectors.toList());
        return dtos;
    }

    @Override
    public UserInfo findById(String id) throws UserNotFoundException {
        if (userInfoRepository.existsById(id)) {
            return userInfoRepository.findById(id).get();
        } else {
            throw new UserNotFoundException(USER_NOT_FOUND);
        }
    }

    @Override
    public UserInfo createUser(UserCreateDto dto) throws UnableToSaveUserException, EmailExistException {
        if (userInfoRepository.existsByEmail(dto.getEmail())) {
            throw new EmailExistException(EMAIL_ALREADY_EXIST);
        } else {
            UserInfo userInfo = new UserInfo();
            String password = randomPasswordGenerator();
            userInfo.setFirstName(dto.getFirstName());
            userInfo.setLastName(dto.getLastName());
            userInfo.setEmail(dto.getEmail());
            userInfo.setContact(dto.getContact());
            userInfo.setPassword(encoder.encode(password));
            userInfo.setRole(dto.getRole());
            UserInfo newUser;
            try {
                newUser = userInfoRepository.save(userInfo);

                String templateData = "{\"name\":\"" + userInfo.getFirstName() + " " + userInfo.getLastName() + "\", \"password\":\"" + password + "\"}";
                emailService.sendMail(templateData, NEW_CREATE_USER_TEMPLATE, newUser.getEmail());
            } catch (Exception e) {
                throw new UnableToSaveUserException(SAVE_USER_FAIL + e.getMessage());
            }
            return newUser;
        }
    }

    @Override
    public UserInfo updateUser(UserInfo userInfo) throws EmailExistException {
        validateEmail(userInfo.getEmail(), userInfo.getId());
        return userInfoRepository.save(userInfo);
    }

    private void validateEmail(String email, String id) throws EmailExistException {
        UserInfo user = userInfoRepository.findByEmail(email);
        if (user != null && !user.getId().equalsIgnoreCase(id)) {
            throw new EmailExistException("Email already taken. Please try again");
        }
    }

    @Override
    public void deleteUser(String id) {
        if(userInfoRepository.existsById(id)) {
            userInfoRepository.deleteById(id);
        } else {
            throw new UsernameNotFoundException("User Not Found with id: " + id);
        }
    }

    @Override
    public void forgetPassword(String email) throws SendFailedException {
        UserInfo userInfo = userInfoRepository.findByEmail(email);
        if (userInfo != null) {
            String password = randomPasswordGenerator();
            userInfo.setPassword(encoder.encode(password));
            userInfoRepository.save(userInfo);

            String templateData = "{\"name\":\"" + userInfo.getFirstName() + " " + userInfo.getLastName() + "\", \"password\":\"" + password + "\"}";
            emailService.sendMail(templateData, FORGET_PASSWORD_TEMPLATE, email);
        }
    }

    @Override
    public void changePassword(ChangePasswordDto dto) throws PasswordIncorrectException {
        Optional<UserInfo> userInfo = userInfoRepository.findById(dto.getId());
        if (userInfo.isPresent()) {
            UserInfo user = userInfo.get();
            if (encoder.matches(dto.getCurrentPassword(), user.getPassword())) {
                user.setPassword(encoder.encode(dto.getNewPassword()));
                userInfoRepository.save(user);
            } else {
                throw new PasswordIncorrectException(CURRENT_PASSWORD_MISMATCH);
            }
        } else {
            throw new UsernameNotFoundException("No user found");
        }
    }

    private String randomPasswordGenerator() {
        String upperCaseRandom = RandomStringUtils.random(2, 65, 90, true, true);
        String lowerCaseRandom = RandomStringUtils.random(2, 97, 122, true, true);
        String numberRandom = RandomStringUtils.randomNumeric(2);
        String specialCharRandom = RandomStringUtils.random(2, 33, 47, false, false);
        return upperCaseRandom.concat(numberRandom).concat(specialCharRandom).concat(lowerCaseRandom);
    }
}
