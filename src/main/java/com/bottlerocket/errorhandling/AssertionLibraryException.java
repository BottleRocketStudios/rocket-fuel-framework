package com.bottlerocket.errorhandling;

/**
 * Created by ford.arnett on 10/15/18
 */
public class AssertionLibraryException extends Exception {
    public AssertionLibraryException() {
        super();
    }

    public AssertionLibraryException(String message) {
        super(message);
    }

    public AssertionLibraryException(String message, Throwable cause) {
        super(message, cause);
    }

    public AssertionLibraryException(Throwable cause) {
        super(cause);
    }

}
