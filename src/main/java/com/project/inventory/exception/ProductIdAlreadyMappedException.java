package com.project.inventory.exception;

public class ProductIdAlreadyMappedException extends RuntimeException{
    public ProductIdAlreadyMappedException(String message) {
        super(message);
    }
}
