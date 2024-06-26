package com.bottlerocket.errorhandling;

/**
 * Created by automation on 5/20/16.
 */
public class WebDriverWrapperException extends Exception {

        public WebDriverWrapperException() {
            super();
        }

        public WebDriverWrapperException(String message) {
            super(message);
        }

        public WebDriverWrapperException(String message, Throwable cause) {
            super(message, cause);
        }

        public WebDriverWrapperException(Throwable cause) {
            super(cause);
        }
}
