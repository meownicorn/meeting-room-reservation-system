package com.jolyn.meetingroomreservationsystem.service;

import jakarta.mail.SendFailedException;
import org.springframework.stereotype.Service;

@Service
public interface EmailService {
    public void sendMail(String templateData, String templateName, String email) throws SendFailedException;
}
