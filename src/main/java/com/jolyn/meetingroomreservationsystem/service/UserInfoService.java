package com.jolyn.meetingroomreservationsystem.service;

import com.jolyn.meetingroomreservationsystem.domain.UserInfo;
import com.jolyn.meetingroomreservationsystem.domain.dto.ChangePasswordDto;
import com.jolyn.meetingroomreservationsystem.domain.dto.UserCreateDto;
import com.jolyn.meetingroomreservationsystem.domain.dto.UserInfoDto;
import com.jolyn.meetingroomreservationsystem.exception.EmailExistException;
import com.jolyn.meetingroomreservationsystem.exception.PasswordIncorrectException;
import com.jolyn.meetingroomreservationsystem.exception.UnableToSaveUserException;
import com.jolyn.meetingroomreservationsystem.exception.UserNotFoundException;
import jakarta.mail.SendFailedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserInfoService {
    List<UserInfoDto> getAllUserInfo();
    UserInfo findById(String id) throws UserNotFoundException;
    UserInfo createUser(UserCreateDto dto) throws UnableToSaveUserException, SendFailedException, EmailExistException;
    UserInfo updateUser(UserInfo userInfo) throws EmailExistException;
    void deleteUser(String id);
    void forgetPassword(String email) throws SendFailedException;
    void changePassword(ChangePasswordDto dto) throws PasswordIncorrectException;
}
