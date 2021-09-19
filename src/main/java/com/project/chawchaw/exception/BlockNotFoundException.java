package com.project.chawchaw.exception;

public class BlockNotFoundException extends RuntimeException {
    public BlockNotFoundException(String msg, Throwable t) {
        super(msg, t);
    }

    public BlockNotFoundException(String msg) {
        super(msg);
    }

    public BlockNotFoundException() {
        super();
    }
}