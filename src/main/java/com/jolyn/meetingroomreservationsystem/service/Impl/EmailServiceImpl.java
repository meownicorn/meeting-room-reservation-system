package com.jolyn.meetingroomreservationsystem.service.Impl;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.SendTemplatedEmailRequest;
import com.jolyn.meetingroomreservationsystem.service.EmailService;
import jakarta.mail.SendFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.jolyn.meetingroomreservationsystem.exception.ExceptionHandling.SEND_EMAIL_FAIL;

@Service
public class EmailServiceImpl implements EmailService {
    @Value("${aws.ses.access-key}")
    private String accessKey;

    @Value("${aws.ses.secret-key}")
    private String secretKey;

    @Value("${aws.ses.region}")
    private String region;

    @Value("${aws.ses.smtp.sender}")
    private String sender;

    private Logger log = LoggerFactory.getLogger(getClass());


    public void sendMail(String templateData, String templateName, String email) throws SendFailedException {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder
                .standard().withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(region).build();

        Destination destination = new Destination();
        List<String> toAddresses = new ArrayList<String>();

        toAddresses.add(email);

        destination.setToAddresses(toAddresses);
        SendTemplatedEmailRequest templatedEmailRequest = new SendTemplatedEmailRequest();
        templatedEmailRequest.withDestination(destination);
        templatedEmailRequest.withTemplate(templateName);
        templatedEmailRequest.withTemplateData(templateData);
        templatedEmailRequest.withSource(sender);

        try {
            client.sendTemplatedEmail(templatedEmailRequest);
        } catch (Exception e) {
            throw new SendFailedException(SEND_EMAIL_FAIL + e);
        }

    }
}
