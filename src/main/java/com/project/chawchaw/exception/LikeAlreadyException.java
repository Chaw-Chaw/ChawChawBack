package com.project.chawchaw.exception;

public class LikeAlreadyException extends RuntimeException {
    public LikeAlreadyException(String msg, Throwable t) {
        super(msg, t);
    }

    public LikeAlreadyException(String msg) {
        super(msg);
    }

    public LikeAlreadyException() {
        super();
    }
}