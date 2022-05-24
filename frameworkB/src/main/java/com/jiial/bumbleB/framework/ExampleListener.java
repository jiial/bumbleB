package com.jiial.bumbleB.framework;

import com.jiial.bumbleB.reporting.HtmlReportBuilder;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;

import java.util.List;

/**
 * Listener class that passes information about test execution to {@link HtmlReportBuilder}.
 */
public class ExampleListener implements TestExecutionListener {

    private TestExecutionResult latestResult;

    @Override
    public void testPlanExecutionFinished(TestPlan testPlan) {
        updateResults(latestResult);
        HtmlReportBuilder.generateReport();
    }

    @Override
    public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
        latestResult = testExecutionResult;
        updateResults(testExecutionResult);
    }

    private void updateResults(TestExecutionResult result) {
        List<HtmlReportBuilder.Example> examples = HtmlReportBuilder.examples.get(HtmlReportBuilder.prevClass);
        if (examples != null) {
            for (HtmlReportBuilder.Example example : examples) {
                if (example.getResult() == null) {
                    example.setResult(result.getStatus());
                    break;
                }
            }
        }
    }
}
