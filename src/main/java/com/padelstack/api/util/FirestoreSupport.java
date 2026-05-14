package com.padelstack.api.util;

import com.google.api.core.ApiFuture;

public final class FirestoreSupport {

    private FirestoreSupport() {
    }

    public static <T> T await(ApiFuture<T> future) {
        try {
            return future.get();
        } catch (Exception ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            if (ex instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            throw new IllegalStateException("Firestore operation failed", ex);
        }
    }
}
