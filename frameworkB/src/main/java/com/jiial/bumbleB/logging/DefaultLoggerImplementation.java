package com.jiial.bumbleB.logging;

public class DefaultLoggerImplementation implements Logger {

    @Override
    public void log(String message) {
        System.out.println(message);
    }
}
