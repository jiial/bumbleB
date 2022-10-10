package com.jiial.bumbleB.reporting;

import com.jiial.bumbleB.framework.Framework;
import com.jiial.bumbleB.framework.Framework.ExampleDefinition;
import j2html.tags.specialized.TrTag;
import org.junit.platform.engine.TestExecutionResult.Status;

import java.io.*;
import java.util.*;

import static j2html.TagCreator.*;

/**
 * Class for generating a basic HTML test report.
 */
public class HtmlReportBuilder {

    public static Map<String, List<Example>> examples = new HashMap<>();
    public static String prevClass;
    private static final String PATH_TO_REPORTS = "target/bumbleB_reports/";
    private static final String REPORT_SUFFIX = "_report.html";

    public static synchronized void generateReport() {
        try {
            File f = new File(PATH_TO_REPORTS);
            if (!f.exists()) {
                f.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<TrTag> exampleRows = createExampleRows();
        String whatToWrite = createHtml(exampleRows);
        try {
            File f = new File(getReportPath());
            Writer writer = new PrintWriter(f);
            writer.write(whatToWrite);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void add(ExampleDefinition definition, double executionTime) {
        String className = definition.getTestClass();
        List<Example> examplesForClass = examples.get(className);
        Example example = new Example(definition, executionTime);
        if (examplesForClass == null) {
            examples.put(className, List.of(example));
        } else {
            List<Example> newList = new ArrayList<>(examplesForClass);
            newList.add(example);
            examples.put(className, newList);
        }
        prevClass = className;
    }

    /**
     * Contains information about examples that are used in reports. The result field is always initially null,
     * but the value is updated by {@link com.jiial.bumbleB.framework.ExampleListener} after the execution is finished.
     */
    public static class Example {

        private final ExampleDefinition definition;
        private Status result;
        private final double executionTime;

        public Example(ExampleDefinition definition, double executionTime) {
            this.definition = definition;
            this.executionTime = executionTime;
        }

        public Status getResult() {
            return this.result;
        }

        public void setResult(Status result) {
            this.result = result;
        }
    }

    private static String getReportPath() {
        return PATH_TO_REPORTS + Framework.state.get().getPackageName() + REPORT_SUFFIX;
    }

    private static List<TrTag> createExampleRows() {
        List<TrTag> trs = new ArrayList<>();
        examples.forEach((k, v) ->
                trs.addAll(List.of(
                        v.stream().map(example ->
                                tr(attrs(".example"),
                                        td(
                                                details(
                                                        summary(h3(example.definition.getName())).attr("result", example.result.toString()),
                                                        each(example.definition.getSteps(), step ->
                                                                div(
                                                                        b(step.getType() + " " + step.getStepDefinition()).attr("result", step.getPassed()),
                                                                        br()
                                                                )
                                                        )
                                                )
                                        ),
                                        td(k),
                                        td(String.valueOf(example.result)).attr("result", example.result.toString()),
                                        td(example.executionTime + " s")
                                )
                        ).toArray(TrTag[]::new)
                ))
        );
        return trs;
    }

    private static String createHtml(List<TrTag> trs) {
        return html(
                head(
                        title("Test results"),
                        style(css)
                ),
                body(
                        h1("Test results"),
                        table(
                                thead(
                                        tr(
                                                th("Example").withStyle("width:60%"),
                                                th("Story"),
                                                th("Result"),
                                                th("Time")
                                        )
                                ),
                                tbody(
                                        each(trs, tr -> tr)
                                )
                        )
                )
        ).render();
    }

    // The css style is defined as a string, since referencing a separate css file from other projects caused issues
    private static final String css = "td[result='SUCCESSFUL'] {\n" +
            "    color: darkgreen;\n" +
            "}\n" +
            "\n" +
            "td[result='ABORTED'] {\n" +
            "    color: red;\n" +
            "}\n" +
            "\n" +
            "td[result='FAILED'] {\n" +
            "    color: red;\n" +
            "}\n" +
            "\n" +
            "summary[result='SUCCESSFUL'] {\n" +
            "    color: darkgreen;\n" +
            "}\n" +
            "\n" +
            "summary[result='ABORTED'] {\n" +
            "    color: red;\n" +
            "}\n" +
            "\n" +
            "summary[result='FAILED'] {\n" +
            "    color: red;\n" +
            "}\n" +
            "\n" +
            "details summary {\n" +
            "    cursor: pointer;\n" +
            "}\n" +
            "\n" +
            "details summary > * {\n" +
            "    display: inline;\n" +
            "}\n" +
            "\n" +
            "table {\n" +
            "    border-collapse: collapse;\n" +
            "    width: 100%;\n" +
            "}\n" +
            "\n" +
            "th, td {\n" +
            "    padding: 8px;\n" +
            "    text-align: left;\n" +
            "    border-bottom: 1px solid palegreen;\n" +
            "}\n" +
            "\n" +
            "tr:hover {\n" +
            "    background-color: lightgreen;\n" +
            "}\n" +
            "\n" +
            "th {\n" +
            "    text-align: left;\n" +
            "    background-color: darkgreen;\n" +
            "    color: palegreen;\n" +
            "    font-size: larger;\n" +
            "}\n" +
            "\n" +
            "tr {\n" +
            "    background-color: darkseagreen;\n" +
            "}\n" +
            "\n" +
            "body {\n" +
            "    background-color: darkolivegreen;\n" +
            "}\n" +
            "\n" +
            "h1 {\n" +
            "    color: palegreen;\n" +
            "    border-bottom: 1px solid;\n" +
            "}\n" +
            "\n" +
            "b[result=true] {\n" +
            "    color: black;\n" +
            "}\n" +
            "\n" +
            "b[result=false] {\n" +
            "    color: red;\n" +
            "}";
}
