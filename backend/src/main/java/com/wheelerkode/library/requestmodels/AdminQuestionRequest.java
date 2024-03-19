package com.wheelerkode.library.requestmodels;

import lombok.Data;

import java.util.UUID;

@Data
public class AdminQuestionRequest {
    private UUID id;
    private String response;
}
