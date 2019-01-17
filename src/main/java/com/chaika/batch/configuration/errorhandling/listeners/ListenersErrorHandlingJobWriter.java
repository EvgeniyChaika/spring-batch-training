package com.chaika.batch.configuration.errorhandling.listeners;

import com.chaika.batch.utils.exception.CustomException;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

/**
 * Created by echaika on 17.01.2019
 */
public class ListenersErrorHandlingJobWriter implements ItemWriter<String> {

    private int attemptCount = 0;

    @Override
    public void write(List<? extends String> items) throws Exception {
        for (String item : items) {
            if ("-84".equalsIgnoreCase(item)) {
                throw new CustomException("Write failed. Attempt: " + attemptCount);
            } else {
                System.out.println(item);
            }
        }
    }
}
