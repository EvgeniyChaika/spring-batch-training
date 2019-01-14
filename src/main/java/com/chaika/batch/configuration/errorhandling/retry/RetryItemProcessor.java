package com.chaika.batch.configuration.errorhandling.retry;

import com.chaika.batch.utils.exception.CustomRetryableException;
import org.springframework.batch.item.ItemProcessor;

/**
 * Created by echaika on 14.01.2019
 */
public class RetryItemProcessor implements ItemProcessor<String, String> {

    private boolean retry = false;
    private int attemptCount = 0;

    @Override
    public String process(String item) throws Exception {
        System.out.println("Processing item" + item);
        if (retry && item.equalsIgnoreCase("42")) {
            attemptCount++;

            if (attemptCount >= 5) {
                System.out.println("Success!");
                retry = false;
                return String.valueOf(Integer.valueOf(item) * -1);
            } else {
                System.out.println("Processing of item " + item + " failed");
                throw new CustomRetryableException("Process failed. Attempt: " + attemptCount);
            }
        } else {
            return String.valueOf(Integer.valueOf(item) * -1);
        }
    }

    public void setRetry(boolean retry) {
        this.retry = retry;
    }
}
