package org.projetmanager.projetmanager.utils;

import org.projetmanager.projetmanager.service.RunService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class StreamGobbler implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamGobbler.class);

    private final InputStream inputStream;
    private final Consumer<String> consumer;
    private final List<StreamGobblerLog> streamGobblerLogs = new CopyOnWriteArrayList<>();
    private final AtomicLong counter = new AtomicLong(1);

    public StreamGobbler(InputStream inputStream, Consumer<String> consumer, boolean error) {
        this.inputStream = inputStream;
        this.consumer = (x) -> {
            streamGobblerLogs.add(new StreamGobblerLog(counter.getAndIncrement(), error, LocalDateTime.now(), x));
            consumer.accept(x);
        };
    }

    @Override
    public void run() {
        try {
            new BufferedReader(new InputStreamReader(inputStream))
                    .lines()
                    .forEach(consumer);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                LOGGER.error("Error", e);
            }
        }
    }
}
