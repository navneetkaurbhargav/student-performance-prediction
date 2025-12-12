package com.studentperformance.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import com.studentperformance.model.dto.RetrainingRequest;

@Service
public class JmsService {
    
    @Autowired
    private JmsTemplate jmsTemplate;
    
    public void sendRetrainingRequest(RetrainingRequest request) {
        jmsTemplate.convertAndSend("model.training.queue", request);
    }
}