package org.example.emailservice.consumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.emailservice.dtos.SignUpEventDTO;
import org.example.emailservice.utls.EmailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;

@Service
public class SignUpEvenConsumer {

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(
            topics ="signUpEventTopic",
            groupId = "signUpEventConsumerGroup"
    )
    public void handleSignUpEvent(String message) throws JsonProcessingException {
        SignUpEventDTO signUpEventDTO = objectMapper.readValue(message, SignUpEventDTO.class);
        String to = signUpEventDTO.getTo();
        String from = signUpEventDTO.getFrom();
        String subject = signUpEventDTO.getSubject();
        String body = signUpEventDTO.getBody();

        System.out.println("TLSEmail Start");
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
        props.put("mail.smtp.port", "587"); //TLS Port
        props.put("mail.smtp.auth", "true"); //enable authentication
        props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS

        //create Authenticator object to pass in Session.getInstance argument
        Authenticator auth = new Authenticator() {
            //override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, "tcdslabwpvberxjg");
            }
        };
        Session session = Session.getInstance(props, auth);

        EmailUtil.sendEmail(session, to,subject, body);
    }


}
