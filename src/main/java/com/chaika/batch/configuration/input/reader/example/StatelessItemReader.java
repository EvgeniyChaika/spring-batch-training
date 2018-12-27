package com.chaika.batch.configuration.input.reader.example;

import org.springframework.batch.item.ItemReader;

import java.util.Iterator;
import java.util.List;

/**
 * Created by echaika on 27.12.2018
 */
public class StatelessItemReader implements ItemReader<String> {

    private final Iterator<String> data;

    public StatelessItemReader(List<String> data) {
        this.data = data.iterator();
    }

    @Override
    public String read() throws Exception {
        String result = null;
        if (this.data.hasNext()) {
            result = this.data.next();
        }
        return result;
    }
}
