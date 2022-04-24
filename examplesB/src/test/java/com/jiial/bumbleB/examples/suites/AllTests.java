package com.jiial.bumbleB.examples.suites;

import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Execution(ExecutionMode.CONCURRENT)
@SelectPackages({
        "com.jiial.framework.examples.integrationTests",
        "com.jiial.framework.examples.uiTests"
})
@Suite
public class AllTests {
}
