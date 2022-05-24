package com.jiial.bumbleB.framework;

/**
 * StateHolder used to pass information between {@link Framework} and {@link com.jiial.bumbleB.aspects.StepAspect}.
 * Also used to store information for {@link com.jiial.bumbleB.reporting.HtmlReportBuilder}.
 */
public class StateHolder {

    // Variables for framework
    private Utils.StepType prev; // Used to get the correct step type for "and"-steps
    private String stepDefinition; // Annotation value, set by StepAspect
    private Object[] args; // Arguments to call the referenced method within a step with

    // Variables for test reports
    // Also referenced in framework, but the main reason is to provide information for the reports
    private String className; // The class name for the example being run, used in reports
    private String packageName; // The package name for the example being run, used in reports
    private Boolean stepPassed; // Result of a single step, used in reports

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

    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Boolean getStepPassed() {
        return this.stepPassed;
    }

    public void setStepPassed(boolean stepPassed) {
        this.stepPassed = stepPassed;
    }
}
