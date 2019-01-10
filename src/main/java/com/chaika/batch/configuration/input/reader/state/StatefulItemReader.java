package com.chaika.batch.configuration.input.reader.state;

import org.springframework.batch.item.*;

import java.util.List;

/**
 * Created by echaika on 10.01.2019
 */
public class StatefulItemReader implements ItemStreamReader<String> {

    private final List<String> items;
    private int curIndex;
    private boolean restart = false;

    public StatefulItemReader(List<String> items) {
        this.items = items;
        this.curIndex = 0;
    }

    @Override
    public String read() throws Exception {
        String item = null;

        if (curIndex < items.size()) {
            item = items.get(curIndex);
            curIndex++;
        }
        if (curIndex == 42 && !restart) {
            throw new RuntimeException("Stopped by custom exeption");
        }
        return item;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        if (executionContext.containsKey("curIndex")) {
            curIndex = executionContext.getInt("curIndex");
            restart = true;
        } else {
            curIndex = 0;
            executionContext.put("curIndex", curIndex);
        }
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        executionContext.put("curIndex", curIndex);
    }

    @Override
    public void close() throws ItemStreamException {
    }
}
