package com.chaika.batch.configuration.listeners;

import org.springframework.batch.core.annotation.AfterChunk;
import org.springframework.batch.core.annotation.BeforeChunk;
import org.springframework.batch.core.scope.context.ChunkContext;

/**
 * Created by echaika on 26.12.2018
 */
public class CustomChunkListener {

    @BeforeChunk
    public void beforeChunk(ChunkContext context) {
        System.out.println(">> Before the chunk");
    }

    @AfterChunk
    public void afterChunk(ChunkContext context) {
        System.out.println("<< After the chunk");
    }
}
