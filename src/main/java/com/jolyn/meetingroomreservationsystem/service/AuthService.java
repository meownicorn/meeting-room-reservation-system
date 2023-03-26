package com.jolyn.meetingroomreservationsystem.service;

import com.jolyn.meetingroomreservationsystem.domain.UserInfo;
import com.jolyn.meetingroomreservationsystem.domain.dto.LoginDto;
import com.jolyn.meetingroomreservationsystem.domain.dto.RegisterDto;
import com.jolyn.meetingroomreservationsystem.exception.EmailExistException;
import com.jolyn.meetingroomreservationsystem.exception.UnableToSaveUserException;
import jakarta.mail.SendFailedException;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    UserInfo register(RegisterDto dto) throws EmailExistException, UnableToSaveUserException, SendFailedException;
    HttpHeaders login(LoginDto dto);
}
