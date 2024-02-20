package org.example.task1;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class DefaultHandler implements Handler {

    private final Client client;

    public DefaultHandler(Client client) {
        this.client = client;
    }

    @Override
    public ApplicationStatusResponse performOperation(String id) {
        AtomicInteger retriesCount = new AtomicInteger(0);

        CompletableFuture<Response> future1 = CompletableFuture.supplyAsync(() -> client.getApplicationStatus1(id));
        CompletableFuture<Response> future2 = CompletableFuture.supplyAsync(() -> client.getApplicationStatus2(id));
        CompletableFuture<Response> responseCompletableFuture = future1.applyToEither(future2, Function.identity());

        try {
            Response response = responseCompletableFuture.get(15, TimeUnit.SECONDS);
            var applicationStatusResponse = switch (response) {
                case Response.Success success ->
                        new ApplicationStatusResponse.Success(success.applicationId(), success.applicationStatus());
                case Response.RetryAfter retryAfter -> {
                    retriesCount.incrementAndGet();
                }
                case Response.Failure failure -> new ApplicationStatusResponse.Failure(null, retriesCount.get());
            };
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        // todo обработка ответов сервисов и преобразование полученных данных в ответ нового сервиса
    }
}