package com.cbec.messager.controller.internal;

import com.cbec.internal.api.messager.MessageDTO;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;
@RunWith(SpringRunner.class)
@SpringBootTest
public class MessageApiImplTest {
    @Autowired
    private MessageApiImpl messageApi;

    @Test
    @Ignore
    public void sendMail() {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setDestination("");
        messageDTO.setMessage("test");
        messageApi.sendMail(messageDTO);
    }
}