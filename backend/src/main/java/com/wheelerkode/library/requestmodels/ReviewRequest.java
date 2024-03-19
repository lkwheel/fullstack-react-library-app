package com.wheelerkode.library.requestmodels;

import lombok.Data;

import java.util.Optional;
import java.util.UUID;

@Data
public class ReviewRequest {

    private double rating;
    private UUID bookId;
    private Optional<String> reviewDescription;
}
