package org.example.task2;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class DefaultHandler implements Handler {

    private static final int MILLIS_TIMEOUT = 100;

    private final Client client;
//    private final BlockingQueue<Event> eventQueue;

    public DefaultHandler(Client client) {
        this.client = client;
//        this.eventQueue = new LinkedBlockingQueue<>();
    }

    @Override
    public Duration timeout() {
        return Duration.ofMillis(MILLIS_TIMEOUT);
    }

    @Override
    public void performOperation() {
        // todo переписать на LinkedBlockingQueue?
        while (true) {
            Event data = client.readData();
            CompletableFuture[] futures = data.recipients().stream()
                    .map(recipient -> sendDataAsync(recipient, data.payload()))
                    .toArray(CompletableFuture<?>[]::new);

            CompletableFuture.allOf(futures)
                    .join();
        }
    }

    private CompletableFuture<Result> sendDataAsync(Address address, Payload payload) {
        return CompletableFuture.supplyAsync(() -> client.sendData(address, payload));
    }
}
