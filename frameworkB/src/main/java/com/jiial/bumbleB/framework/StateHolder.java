package com.jiial.bumbleB.framework;

/**
 * StateHolder used to pass information between {@link Framework} and {@link com.jiial.bumbleB.aspects.StepAspect}.
 */
public class StateHolder {
    private Utils.StepType prev;
    private String stepDefinition;
    private Object[] args;

    public Utils.StepType getPrev() {
        return prev;
    }

    public void setPrev(Utils.StepType prev) {
        this.prev = prev;
    }

    public String getStepDefinition() {
        return stepDefinition;
    }

    public void setStepDefinition(String stepDefinition) {
        this.stepDefinition = stepDefinition;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }
}
