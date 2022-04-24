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

        // Save annotation values to external file to support multiple modules
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
            MethodTree methodTree = scanner.scan(element, this.trees);
            methodTree.getBody().getStatements().forEach(
                    statement -> {
                        whatToWrite.add(getTitle(statement.toString()));
                        whatToWrite.addAll(getDefinitions(statement.toString()));

                    }
            );

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

        public MethodTree scan(Element methodElement, Trees trees) {
            // Reset the list for each methodElement annotated with @Scenario
            methodTrees = new ArrayList<>();
            // Scan the tree from the method's position
            this.scan(trees.getPath(methodElement), trees);
            // Size should always be 1 as we are visiting a single method
            assert methodTrees.size() == 1;
            return methodTrees.get(0);
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
        String[] temp = statement.split("name\\(");
        String name = temp[1].split("\\)")[0]
                .replaceAll("\"", "")
                .replaceAll("\\\\", "");
        return "Example: " + name + "\n";
    }



    private List<String> getDefinitions(String statement) {
        statement = statement.replaceAll(" ", "");
        statement = statement.replaceAll("\n", "");
        String splitStatement = statement.split("steps\\(")[1].split("\\)\\)\\.")[0];
        String[] splitByComma = splitStatement.split(",");
        List<String> initialSteps = new ArrayList<>(Arrays.asList(splitByComma));
        List<String> actualSteps = new ArrayList<>();
        for (String initialStep : initialSteps) {
            if (initialStep.contains("::")) {
                String[] temp = initialStep.split("\\(");
                String[] temp2 = temp[1].split("::");
                String stepType = temp[0].toUpperCase().charAt(0) + temp[0].substring(1);
                String methodName = temp2[1].replaceAll("\\)", "").split("\\.")[0];
                String annotationValue = stepMappings.get(methodName);
                actualSteps.add(stepType + " " + annotationValue);
            }
        }
        return actualSteps;
    }
}