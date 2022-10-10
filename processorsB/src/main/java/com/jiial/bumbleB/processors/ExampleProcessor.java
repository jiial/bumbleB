package com.jiial.bumbleB.processors;


import com.jiial.bumbleB.annotations.Example;
import com.jiial.bumbleB.annotations.Step;
import com.sun.source.tree.MethodTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import java.io.*;
import java.util.*;

public class ExampleProcessor extends AbstractProcessor {

    private Map<String, String> stepMappings;
    private Trees trees;
    private List<String> testNames;

    @Override
    public void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.stepMappings = new HashMap<>();
        this.trees = Trees.instance(processingEnv);
        this.testNames = new ArrayList<>();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        // Get all Step-annotation values and save them to stepMappings with the annotated method's name as the key
        for (Element element : roundEnv.getElementsAnnotatedWith(Step.class)) {
            assert element.getKind().equals(ElementKind.METHOD);
            stepMappings.put(element.getSimpleName().toString(), element.getAnnotation(Step.class).value());
        }

        // Save annotation values to external file to support multiple modules (required for unit testing)
        try {
            Writer writer = new PrintWriter(new FileOutputStream("target/mappings.txt", true));
            stepMappings.forEach((k, v) -> {
                try {
                    writer.append(k).append("$").append(v).append("\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Update the map by reading the external annotation file
        try {
            File mappings = new File("target/mappings.txt");
            Scanner reader = new Scanner(mappings);
            while (reader.hasNextLine()) {
                String[] temp = reader.nextLine().split("\\$");
                if (!stepMappings.containsKey(temp[0])) {
                    stepMappings.put(temp[0], temp[1]);
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        for (Element element : roundEnv.getElementsAnnotatedWith(Example.class)) {
            List<String> whatToWrite = new ArrayList<>();
            assert element.getKind().equals(ElementKind.METHOD);
            MethodScanner scanner = new MethodScanner();

            // Find called method names from the statements inside the method body
            List<MethodTree> methodTrees = scanner.scan(element, this.trees);
            for (MethodTree methodTree : methodTrees) {
                methodTree.getBody().getStatements().forEach(
                        statement -> {
                            String title = getTitle(statement.toString());
                            String narrative = getNarrative(statement.toString());
                            List<String> steps = getDefinitions(statement.toString());
                            if (title != null) {
                                whatToWrite.add(title);
                            }
                            if (narrative != null) {
                                whatToWrite.add(narrative);
                            }
                            if (steps != null) {
                                whatToWrite.addAll(steps);
                            }

                        }
                );
            }

            // Create folder structure
            String fileName = element.getEnclosingElement().getSimpleName().toString();
            PackageElement packageElement = (PackageElement) element.getEnclosingElement().getEnclosingElement();
            String filePackage = packageElement.getQualifiedName().toString();
            String[] folders = filePackage.split("\\.");
            String pathForFile = "src/test/resources/";
            for (String folder : folders) {
                pathForFile = pathForFile + folder + "/";
                new File(pathForFile).mkdirs();
            }

            // Create the text file
            try {
                boolean appendOrNo = testNames.contains(fileName);
                testNames.add(fileName);

                Writer writer = new PrintWriter(new FileOutputStream(pathForFile + fileName + ".txt", appendOrNo));

                // Write the lines to the generated files
                for (int i = 0; i < whatToWrite.size(); i++) {

                    if (appendOrNo && whatToWrite.get(i).startsWith("Example:")) {
                        writer.append("\n\n\n");
                    }

                    writer.append(whatToWrite.get(i));

                    if (i < whatToWrite.size() - 1) {
                        writer.append("\n");
                    }
                }

                // Close the writer
                writer.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }



        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of("com.jiial.bumbleB.annotations.Step", "com.jiial.bumbleB.annotations.Example");
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_17;
    }

    private static class MethodScanner extends TreePathScanner<List<MethodTree>, Trees> {
        private List<MethodTree> methodTrees;

        public List<MethodTree> scan(Element methodElement, Trees trees) {
            // Reset the list for each methodElement annotated with @Scenario
            methodTrees = new ArrayList<>();
            // Scan the tree from the method's position
            this.scan(trees.getPath(methodElement), trees);
            // Return the resulting list of method trees
            return methodTrees;
        }

        @Override
        public List<MethodTree> scan(TreePath treePath, Trees trees) {
            super.scan(treePath, trees);
            return methodTrees;
        }

        @Override
        public List<MethodTree> visitMethod(MethodTree methodTree, Trees trees) {
            // This should get called during the scan, allowing us to save the method tree to the methodTrees variable
            methodTrees.add(methodTree);
            return super.visitMethod(methodTree, trees);
        }
    }

    private String getTitle(String statement) {
        if (statement.contains("name(")) {
            String[] temp = statement.split("name\\(");
            String name = temp[1].split("\\)")[0]
                    .replaceAll("\"", "")
                    .replaceAll("\\\\", "");
            return "Example: " + name + "\n";
        }
        return null;
    }

    private String getNarrative(String statement) {
        if (statement.contains("narrative(")) {
            String[] temp = statement.split("narrative\\(");
            String name = temp[1].split("\\)")[0]
                    .replaceAll("\"", "")
                    .replaceAll("\\\\", "");
            return "Narrative: " + name + "\n";
        }
        return null;
    }

    private List<String> getDefinitions(String statement) {
        if (statement.contains(".build()") && statement.contains(".run();")) {
            statement = statement.trim();
            statement = statement.replaceAll("\n", "");
            String exampleMethods = "build|narrative|name";
            String regexForStepsEnding = "\\)\\)\\." + "(" + exampleMethods + ")";
            String splitStatement = statement.split("teps\\(")[1].split(regexForStepsEnding)[0];
            String[] splitByComma = splitStatement.split(",");
            List<String> initialSteps = new ArrayList<>(Arrays.asList(splitByComma));
            List<String> actualSteps = new ArrayList<>();
            String prevType = null;
            for (String initialStep : initialSteps) {
                if (initialStep.contains("describeAs(")) {
                    String stepType;
                    if (initialStep.contains("::")) {
                        stepType = initialStep.split("\\(")[0].trim();
                        stepType = stepType.toUpperCase().charAt(0) + stepType.substring(1);
                    } else {
                        stepType = prevType;
                    }
                    String description = initialStep.split("describeAs\\(")[1].split("\\)")[0].trim().replaceAll("\"", "");
                    actualSteps.add(stepType + " " + description);
                } else if (initialStep.contains("::")) {
                    String[] temp = initialStep.split("\\(", 2);
                    String[] temp2 = temp[1].split("::");
                    String stepType = temp[0].trim().toUpperCase().charAt(0) + temp[0].trim().substring(1);
                    // Only update previous type for legit step types and not method calls related to Step.satisfies()
                    if (!stepType.contains(".") && !stepType.contains("(") && !stepType.contains(")")) {
                        prevType = stepType;
                    }
                    String[] temp3 = temp2[1].replaceAll("\\)", "").split("\\.");
                    String methodName = temp3[0].trim();
                    String because = null;
                    if (temp3.length > 1 && temp3[1] != null && temp3[1].contains("because(")) {
                        because = temp3[1].trim().split("because\\(")[1].split("\\)")[0].replaceAll("\"", "");
                    }
                    String annotationValue = stepMappings.get(methodName);
                    // The annotation value is not found for steps of unit tests as the methods are not annotated
                    if (annotationValue != null) {
                        String actualStep;
                        if (because != null) {
                            actualStep = stepType + " " + annotationValue + "\n\t" + "Because: " + because;
                        } else {
                            actualStep = stepType + " " + annotationValue;
                        }
                        actualSteps.add(actualStep);
                    }
                } else if (initialStep.contains("because(")) {
                    String because = initialStep.split("because\\(")[1].split("\\)")[0].trim().replaceAll("\"", "");
                    int index = actualSteps.size() - 1;
                    actualSteps.set(index, actualSteps.get(index).concat("\n\t" + "Because: " + because));
                } else if (!initialStep.contains("satisfies") && !initialStep.contains("withArgs") && actualSteps.size() > 0) {
                    // Set parameter for previous step
                    int index = actualSteps.size() - 1;
                    String step = actualSteps.get(index);
                    int i = step.indexOf("{");
                    int j = step.indexOf("[");
                    if (step.contains("{") && step.contains("}") && comesFirst(i, j)) {
                        actualSteps.set(index, step.replaceFirst("\\{.*?\\}", cleanUpStepParam(initialStep)));
                    } else if (step.contains("[") && step.contains("]") && comesFirst(j, i)) {
                        actualSteps.set(index, step.replaceFirst("\\[.*?\\]", cleanUpStepParam(initialStep)));
                    }
                }
            }
            return actualSteps;
        }
        return null;
    }

    private boolean comesFirst(int a, int b) {
        if (b == -1) {
            return a != -1;
        } else return b > a;
    }

    private String cleanUpStepParam(String stepParam) {
        return stepParam.trim().replaceAll("\\(", "").replaceAll("\\)", "");
    }
}