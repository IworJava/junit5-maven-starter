package com.iwor.junit.extension;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import java.lang.reflect.Method;

public class PostProcessingExtension implements TestInstancePostProcessor {

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) {
        Method[] methods = testInstance.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(A.class)) {
                System.out.println("post process @A");
            }
        }
    }
}
