package com.chaika.batch.utils.exception;

/**
 * Created by echaika on 14.01.2019
 */
public class CustomRetryableException extends RuntimeException {

    public CustomRetryableException(String message) {
        super(message);
    }
}
