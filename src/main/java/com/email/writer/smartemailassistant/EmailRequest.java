package com.email.writer.smartemailassistant;

import lombok.Data;

@Data
public class EmailRequest {
    private String emailContent;
    private String tone;
}
