package com.email.writer.smartemailassistant.dto;

import lombok.Data;

@Data
public class EmailRequest {
    private String emailContent;
    private String tone;
}
