package com.cbec.messager.controller.internal;

import com.cbec.internal.api.messager.MessageApi;
import com.cbec.internal.api.messager.MessageDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Slf4j
@RestController
@RequestMapping("/internal/message")
public class MessageApiImpl implements MessageApi {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Override
    public void sendMail(@RequestBody MessageDTO message) {
        message.parseDestination().forEach(dest -> {
            try {
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
                helper.setFrom(from);
                helper.setTo(dest);
                helper.setSubject(message.getTitle());
                helper.setText(message.getMessage(), true);
                mailSender.send(mimeMessage);
                log.info("发送邮件到{}成功", message);
            } catch (MessagingException e) {
                log.info("发送邮件到{}失败， 原因: {}", message, e.getMessage());
            }
        });
    }

    @Override
    public void sendWechat(@RequestBody MessageDTO message) {
        throw new UnsupportedOperationException();
    }
}
