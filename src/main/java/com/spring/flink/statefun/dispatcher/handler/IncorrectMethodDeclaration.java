package com.spring.flink.statefun.dispatcher.handler;

public class IncorrectMethodDeclaration extends RuntimeException {

    public IncorrectMethodDeclaration(String message) {
        super(message);
    }
}
