package com.chaika.batch.configuration.errorhandling.skip;

import com.chaika.batch.utils.exception.CustomRetryableException;
import org.springframework.batch.item.ItemProcessor;

/**
 * Created by echaika on 16.01.2019
 */
public class SkipItemProcessor implements ItemProcessor<String, String> {

    private boolean skip = false;
    private int attemptCount = 0;

    @Override
    public String process(String item) throws Exception {
        System.out.println("processing item " + item);
        if (skip && item.equalsIgnoreCase("42")) {
            attemptCount++;
            System.out.println("Processing of item " + item + " failed");
            throw new CustomRetryableException("Process failed. Attempt: " + attemptCount);
        } else {
            return String.valueOf(Integer.valueOf(item) * -1);
        }
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
    }
}
