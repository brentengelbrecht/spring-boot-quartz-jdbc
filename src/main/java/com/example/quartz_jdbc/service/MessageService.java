package com.example.quartz_jdbc.service;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class MessageService implements Job {

    public void sendMessage() {
        System.out.println("[" + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "] Message");
    }

    @Override
    public void execute(JobExecutionContext context) {
        sendMessage();
    }
}
