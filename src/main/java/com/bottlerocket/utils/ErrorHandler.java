package com.bottlerocket.utils;

import com.bottlerocket.errorhandling.OperationsException;
import com.bottlerocket.errorhandling.WebDriverWrapperException;

import java.io.PrintStream;
import java.lang.Exception;

/**
 *
 * Created by ford.arnett on 9/10/15.
 */
public class ErrorHandler {

    /**
     * This is an incredibly basic error print to show the message and stack trace of the exception.
     * A lot can be done to expand error reporting
     *
     * @param ex the exception to print
     */
    public static void printErr(Exception ex) {
        Logger.log("Error occurred: " + ex.getMessage());
        ex.printStackTrace(new PrintStream(System.out));
    }

    public static void printErr(String message, Exception ex){
        if(message != null && !message.equals(""))
          Logger.log(message);
        Logger.log("Error occurred: " + ex.getMessage());
        ex.printStackTrace(new PrintStream(System.out));
    }

    public static void throwOperationsException(String message, Exception ex) throws OperationsException {
        if(message != null && !message.equals("")) {
            throw new OperationsException(message, ex);
        }
        else{
            throw new OperationsException(ex);
        }

    }

    public static void throwWebDriverException(String message, Exception ex) throws WebDriverWrapperException {
        if(message != null && !message.equals("")) {
            throw new WebDriverWrapperException(message, ex);
        }
        else {
            throw new WebDriverWrapperException(ex);
        }
    }
}
