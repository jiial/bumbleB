package com.jiial.bumbleB.examples.extensions;

import com.jiial.bumbleB.logging.Logger;

public class MyCustomLogger implements Logger {

    @Override
    public void log(String message) {
        if (message.isBlank()) {
            System.out.println(message);
        } else {
            System.out.println("[LOGGER] " + message);
        }
    }
}
