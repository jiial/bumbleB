package com.jiial.bumbleB.framework;

public class StateHolder {
    private Framework.StepType prev;
    private String stepDefinition;
    private Object[] args;

    public Framework.StepType getPrev() {
        return prev;
    }

    public void setPrev(Framework.StepType prev) {
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
