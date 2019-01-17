package com.chaika.batch.configuration.errorhandling.listeners;

import org.springframework.batch.core.SkipListener;

/**
 * Created by echaika on 17.01.2019
 */
public class CustomListener implements SkipListener<String, String> {

    @Override
    public void onSkipInRead(Throwable t) {

    }

    @Override
    public void onSkipInWrite(String item, Throwable t) {
        System.out.println(">> Skipping " + item + " because writing it caused the error: " + t.getMessage());
    }

    @Override
    public void onSkipInProcess(String item, Throwable t) {
        System.out.println(">> Skipping " + item + " because processing it caused the error: " + t.getMessage());
    }
}
