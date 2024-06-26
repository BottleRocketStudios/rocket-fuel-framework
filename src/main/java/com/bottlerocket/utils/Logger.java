package com.bottlerocket.utils;



import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;

/**
 * Created by ford.arnett on 11/20/15.
 */
public class Logger {

    public static void log(String message){
        System.out.println(message);
    }

    public static void log(String message, String type){
        if(type != null){
            LocalTime timestamp = LocalTime.now();
            System.out.println("[" + timestamp + "] " + type + ": " + message);
        }
    }

    public enum ComponentType {
        configuration("configuration"),
        frameworkLayer("framework"),
        testLayer("test"),
        mockServer("mockserver"),
        dataloader("dataloader"),
        bash("bash"),
        proguard("proguard"),
        reporter("reporter");


        String type;
        ComponentType(String type) {
            this.type = type;
        }
    }

    public static void logCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        log(formatter.format(new Date()));
    }
}