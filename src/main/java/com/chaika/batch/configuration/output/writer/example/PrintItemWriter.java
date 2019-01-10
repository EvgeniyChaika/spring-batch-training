package com.chaika.batch.configuration.output.writer.example;

import org.springframework.batch.item.ItemWriter;

import java.util.List;

/**
 * Created by echaika on 10.01.2019
 */
public class PrintItemWriter implements ItemWriter<String> {

    @Override
    public void write(List<? extends String> items) throws Exception {
        System.out.println("The size of this chunk was: " + items.size());

        for (String item : items) {
            System.out.println(">> " + item);
        }
    }
}
