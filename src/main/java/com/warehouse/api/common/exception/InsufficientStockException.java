package com.warehouse.api.common.exception;

public class InsufficientStockException extends RuntimeException {

    private final int available;
    private final int requested;

    public InsufficientStockException(int available, int requested) {
        super("Insufficient stock. Available: " + available + ", Requested: " + requested);
        this.available = available;
        this.requested = requested;
    }

    public int getAvailable() {
        return available;
    }

    public int getRequested() {
        return requested;
    }
}
