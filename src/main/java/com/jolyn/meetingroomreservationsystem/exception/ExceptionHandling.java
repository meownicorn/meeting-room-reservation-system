package com.jolyn.meetingroomreservationsystem.exception;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.jolyn.meetingroomreservationsystem.domain.HttpResponse;
import jakarta.mail.MessagingException;
import jakarta.mail.SendFailedException;
import jakarta.persistence.NoResultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.nio.file.AccessDeniedException;
import java.util.Arrays;
import java.util.Objects;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class ExceptionHandling {
    private Logger log = LoggerFactory.getLogger(getClass());
    //For user exception handling
    private static final String INCORRECT_CREDENTIALS = "Username / password incorrect. Please try again.";
    public static final String INTERNAL_SERVER_ERROR_MSG = "An error occurred while processing the request.";
    public static final String METHOD_IS_NOT_ALLOWED = "This request method is not allowed on this endpoint. Please send a '%s request.";
    public static final String NOT_ENOUGHT_PERMISSION = "You do not have enough permission.";
    public static final String PAGE_NOT_FOUND = "This page is not found";
    public static final String SEND_EMAIL_FAIL = "Failed to send an email";

    //For reservation exception handling
    public static final String OVERLAP_RESERVATION = "The selected date/time clashed with current reservations. Please select a new date/time.";
    public static final String EMAIL_ALREADY_EXIST = "Email already taken. Please try again.";
    public static final String SAVE_USER_FAIL = "Failed to save user.";
    public static final String USER_NOT_FOUND = "No user found";
    public static final String CURRENT_PASSWORD_MISMATCH = "Incorrect current password";
    public static final String WRONG_METHOD_USED = "Please use the correct request method";

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<HttpResponse> incorrectCredentialsException() {
        return createHttpResponcse(BAD_REQUEST, INCORRECT_CREDENTIALS);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<HttpResponse> illegalStateException(IllegalStateException exception) {
        log.error(exception.getMessage());
        return createHttpResponcse(BAD_REQUEST, WRONG_METHOD_USED);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<HttpResponse> noHandlerException(NoHandlerFoundException exception) {
        log.error(exception.getMessage());
        return createHttpResponcse(BAD_REQUEST, PAGE_NOT_FOUND);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<HttpResponse> methodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        log.error(exception.getMessage());
        HttpMethod supportedMethod = HttpMethod.valueOf(Arrays.stream(Objects.requireNonNull(exception.getSupportedMethods())).iterator().next());
        return createHttpResponcse(METHOD_NOT_ALLOWED, String.format(METHOD_IS_NOT_ALLOWED, supportedMethod));
    }

    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<HttpResponse> handleAccessDeniedException(Exception exception) {
        if(exception.getMessage().toLowerCase().equalsIgnoreCase("Access is denied")) {
            log.error(exception.getMessage());
            return createHttpResponcse(UNAUTHORIZED, NOT_ENOUGHT_PERMISSION);
        }
        log.error(exception.getMessage());
        return createHttpResponcse(BAD_REQUEST, NOT_ENOUGHT_PERMISSION);
    }

    @ExceptionHandler(EmailExistException.class)
    public ResponseEntity<HttpResponse> emailExistException(EmailExistException exception) {
        log.error(exception.getMessage());
        return createHttpResponcse(BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<HttpResponse> emailNotFoundException(EmailNotFoundException exception) {
        log.error(exception.getMessage());
        return createHttpResponcse(BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<HttpResponse> tokenExpiredException(TokenExpiredException exception) {
        log.error(exception.getMessage());
        return createHttpResponcse(UNAUTHORIZED, exception.getMessage().toUpperCase());
    }

    @ExceptionHandler(NoResultException.class)
    public ResponseEntity<HttpResponse> notFoundException(NoResultException exception) {
        log.error(exception.getMessage());
        return createHttpResponcse(NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(SendFailedException.class)
    public ResponseEntity<HttpResponse> sendEmailFail(MessagingException exception) {
        log.error(exception.getMessage());
        return createHttpResponcse(BAD_REQUEST, SEND_EMAIL_FAIL);
    }

    @ExceptionHandler(UnableToSaveUserException.class)
    public ResponseEntity<HttpResponse> unableToSaveUser(UnableToSaveUserException exception) {
        log.error(exception.getMessage());
        return createHttpResponcse(BAD_REQUEST, SAVE_USER_FAIL);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<HttpResponse> userNotFound(UserNotFoundException exception) {
        log.error(exception.getMessage());
        return createHttpResponcse(BAD_REQUEST, USER_NOT_FOUND);
    }

    @ExceptionHandler(PasswordIncorrectException.class)
    public ResponseEntity<HttpResponse> incorrectPassword(PasswordIncorrectException exception) {
        log.error(exception.getMessage());
        return createHttpResponcse(BAD_REQUEST, CURRENT_PASSWORD_MISMATCH);
    }

    @ExceptionHandler(OverlappingReservationException.class)
    public ResponseEntity<HttpResponse> overlappingReservation(OverlappingReservationException exception) {
        log.error(exception.getMessage());
        return createHttpResponcse(BAD_REQUEST, OVERLAP_RESERVATION);
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<HttpResponse> internalServerException(Exception exception) {
//        log.error(exception.getMessage());
//        return createHttpResponcse(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MSG);
//    }

    private ResponseEntity<HttpResponse> createHttpResponcse(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(), message.toUpperCase()), httpStatus);
    }
}
