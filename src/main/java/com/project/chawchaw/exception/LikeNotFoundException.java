package com.project.chawchaw.exception;

public class LikeNotFoundException extends RuntimeException {
    public LikeNotFoundException(String msg, Throwable t) {
        super(msg, t);
    }
    public LikeNotFoundException(String msg) {
        super(msg);
    }
    public LikeNotFoundException() {
        super();
    }
}