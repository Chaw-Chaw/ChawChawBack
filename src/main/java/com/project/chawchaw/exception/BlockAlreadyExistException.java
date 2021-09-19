package com.project.chawchaw.exception;

public class BlockAlreadyExistException extends RuntimeException {
    public BlockAlreadyExistException(String msg, Throwable t) {
        super(msg, t);
    }

    public BlockAlreadyExistException(String msg) {
        super(msg);
    }

    public BlockAlreadyExistException() {
        super();
    }
}