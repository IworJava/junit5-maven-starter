package com.iwor.junit.extension;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class GlobalExtension implements BeforeAllCallback, AfterAllCallback {

    @Override
    public void beforeAll(ExtensionContext context) {
        System.out.println("Before all callback");
    }

    @Override
    public void afterAll(ExtensionContext context) {
        System.out.println("After all callback");
    }
}
