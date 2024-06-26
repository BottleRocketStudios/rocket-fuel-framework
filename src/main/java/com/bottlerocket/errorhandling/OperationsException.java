package com.bottlerocket.errorhandling;

/**
 *
 * Created by ford.arnett on 5/13/16.
 */
public class OperationsException extends Exception {

    public OperationsException() {
        super();
    }

    public OperationsException(String message) {
        super(message);
    }

    public OperationsException(String message, Throwable cause) {
        super(message, cause);
    }

    public OperationsException(Throwable cause) {
        super(cause);
    }
}
