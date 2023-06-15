package com.iwor.junit.extension;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

public class ConditionalExtension implements ExecutionCondition {

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        return context.getDisplayName().equals("users are empty if no-one is added")
                ? ConditionEvaluationResult.disabled("skipped")
                : ConditionEvaluationResult.enabled("enabled by default");
    }
}
