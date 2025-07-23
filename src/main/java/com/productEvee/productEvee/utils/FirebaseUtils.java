package com.productEvee.productEvee.utils;

import com.google.api.core.ApiFuture;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class FirebaseUtils {

    public static <T> CompletableFuture<T> convertToCompletable(ApiFuture<T> apiFuture) {
        CompletableFuture<T> completableFuture = new CompletableFuture<>();
        Executor executor = Runnable::run;

        apiFuture.addListener(() -> {
            try {
                T result = apiFuture.get();
                completableFuture.complete(result);
            } catch (Exception e) {
                completableFuture.completeExceptionally(e);
            }
        }, executor);

        return completableFuture;
    }
}
