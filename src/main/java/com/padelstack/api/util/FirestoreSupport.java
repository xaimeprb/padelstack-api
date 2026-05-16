package com.padelstack.api.util;

import com.google.api.core.ApiFuture;

/**
 * Clase encargada de gestionar firestore support.
 */
public final class FirestoreSupport {

    /**
     * Crea una instancia de FirestoreSupport.
     */
    private FirestoreSupport() {
    }

    /**
     * Espera el resultado de una operación asíncrona de Firestore.
     *
     * @param future valor recibido por el método.
     * @return resultado devuelto por la operación.
     */
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
