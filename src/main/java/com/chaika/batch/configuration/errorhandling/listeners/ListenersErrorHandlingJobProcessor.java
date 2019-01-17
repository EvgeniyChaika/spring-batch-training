package com.chaika.batch.configuration.errorhandling.listeners;

import com.chaika.batch.utils.exception.CustomException;
import org.springframework.batch.item.ItemProcessor;

/**
 * Created by echaika on 17.01.2019
 */
public class ListenersErrorHandlingJobProcessor implements ItemProcessor<String, String> {

    private int attemptCount = 0;

    @Override
    public String process(String item) throws Exception {
        if ("42".equalsIgnoreCase(item)) {
            throw new CustomException("Process failed. Attempt: " + attemptCount);
        } else {
            return String.valueOf(Integer.valueOf(item) * -1);
        }
    }
}
