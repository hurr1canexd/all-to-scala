package org.example.task2;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayDeque;
import java.util.Queue;

public class DefaultHandler implements Handler {

    private final Client client;
    private final Queue<Event> eventQueue;

    public DefaultHandler(Client client) {
        this.client = client;
        this.eventQueue = new ArrayDeque<>();
    }

    @Override
    public Duration timeout() {
        return Duration.of(100, ChronoUnit.MILLIS);
    }

    @Override
    public void performOperation() {
        Event data = client.readData(); // todo высокая пропускная способность - нужно хранилище?
        for (Address address : data.recipients()) {
            Result result = client.sendData(address, data.payload());
            if (result == Result.ACCEPTED) {
                // отправка адресату считается завершённой
            } else if (result == Result.REJECTED) {
                // отклонены, операцию отправки следует повторить через timeout() => delayed queue?
            }
        }
    }
}
