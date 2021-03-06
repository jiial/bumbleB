package com.jiial.bumbleB.framework;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.jiial.bumbleB.framework.Utils.*;
import com.jiial.bumbleB.logging.Logger;
import com.jiial.bumbleB.reporting.HtmlReportBuilder;
import org.opentest4j.AssertionFailedError;

import static com.jiial.bumbleB.framework.Utils.getLogger;
import static com.jiial.bumbleB.framework.Utils.isSupplier;

/**
 * Main logic for the BDD framework.
 *
 * @author jone.lang
 */
public class Framework {

    // Thread-local state to support parallel execution
    public static final ThreadLocal<StateHolder> state = ThreadLocal.withInitial(StateHolder::new);
    private static final Logger logger = getLogger();

    public static class ExampleDefinition {
        String name;
        List<Step<?, ?>> steps;
        String narrative;
        String testClass;

        public ExampleDefinition(ExampleBuilder builder) {
            this.name = builder.name;
            this.narrative = builder.narrative;
            this.steps = builder.steps;
            this.testClass = builder.testClass;
        }

        public List<Step<?, ?>> getSteps() {
            return this.steps;
        }

        public String getTestClass() {
            return this.testClass;
        }

        public String getName() {
            return this.name;
        }

        public void run() {
            // Get starting time for test
            long startTime = System.nanoTime();
            // Print name and narrative
            logger.log("Example: " + this.name + "\n");
            if (this.narrative != null) {
                logger.log("Narrative: " + this.narrative + "\n");
            }
            for (Step<?, ?> step : steps) {
                // Get starting time for step
                long stepStartTime = System.nanoTime();
                // Set args for StepAspect
                state.get().setArgs(step.args);

                // Cast the method reference to correct type and execute the step
                try {
                    castAndExecute(step);
                    // Set the execution time for the step
                    step.setExecutionTime((double) (System.nanoTime() - stepStartTime) / 1_000_000_000);
                    printStepInformation(step);
                } catch (AssertionFailedError error) {
                    printStepInformation(step);
                    if (Framework.state.get().getStepPassed() != null && !Framework.state.get().getStepPassed()) {
                        markStepFailed(step);
                    }
                    finishExecution(((double) System.nanoTime() - startTime) / 1_000_000_000);
                    throw error;
                }
            }
            finishExecution(((double) System.nanoTime() - startTime) / 1_000_000_000);
        }

        private void castAndExecute(Step<?, ?> step) {
            try {
                Runnable runnable = (Runnable) step.reference;
                runnable.run();
            } catch (ClassCastException ignored) {
            } try {
                // Unit test steps with a return value and a condition to check (no params)
                Supplier<?> supplier = (Supplier<?>) step.reference;
                // Two possible types of assertion methods (represented by consumers), with either 1 or 2 parameters
                // E.g. assertTrue(boolean b) vs. assertEquals(Object expected, Object actual)
                try {
                    OneParamConsumer consumer = (OneParamConsumer) step.condition;
                    consumer.accept(supplier.get());
                } catch (ClassCastException ignored) {
                } catch (AssertionFailedError failed) {
                    handleFailedAssertion(failed);
                } try {
                    TwoParamConsumer consumer = (TwoParamConsumer) step.condition;
                    consumer.accept(step.expected, supplier.get());
                } catch (ClassCastException ignored) {
                } catch (AssertionFailedError failed) {
                    handleFailedAssertion(failed);
                }
            } catch (ClassCastException ignored) {
            } try {
                OneParamConsumer consumer = (OneParamConsumer) step.reference;
                consumer.accept(step.args[0]);
            } catch (ClassCastException ignored) {
            } try {
                TwoParamConsumer consumer = (TwoParamConsumer) step.reference;
                consumer.accept(step.args[0], step.args[1]);
            } catch (ClassCastException ignored) {
            } try {
                ThreeParamConsumer consumer = (ThreeParamConsumer) step.reference;
                consumer.accept(step.args[0], step.args[1], step.args[2]);
            } catch (ClassCastException ignored) {
            } try {
                FourParamConsumer consumer = (FourParamConsumer) step.reference;
                consumer.accept(step.args[0], step.args[1], step.args[2], step.args[3]);
            } catch (ClassCastException ignored) {
            } try {
                FiveParamConsumer consumer = (FiveParamConsumer) step.reference;
                consumer.accept(step.args[0], step.args[1], step.args[2], step.args[3], step.args[4]);
            } catch (ClassCastException ignored) {
            } try {
                SixParamConsumer consumer = (SixParamConsumer) step.reference;
                consumer.accept(step.args[0], step.args[1], step.args[2], step.args[3], step.args[4], step.args[5]);
            } catch (ClassCastException ignored) {
            } try {
                SevenParamConsumer consumer = (SevenParamConsumer) step.reference;
                consumer.accept(step.args[0], step.args[1], step.args[2], step.args[3], step.args[4], step.args[5], step.args[6]);
            } catch (ClassCastException ignored) {
            } try {
                EightParamConsumer consumer = (EightParamConsumer) step.reference;
                consumer.accept(step.args[0], step.args[1], step.args[2], step.args[3], step.args[4], step.args[5], step.args[6], step.args[7]);
            } catch (ClassCastException ignored) {
            } try {
                NineParamConsumer consumer = (NineParamConsumer) step.reference;
                consumer.accept(step.args[0], step.args[1], step.args[2], step.args[3], step.args[4], step.args[5], step.args[6], step.args[7], step.args[8]);
            } catch (ClassCastException ignored) {
            } try {
                TenParamConsumer consumer = (TenParamConsumer) step.reference;
                consumer.accept(step.args[0], step.args[1], step.args[2], step.args[3], step.args[4], step.args[5], step.args[6], step.args[7], step.args[8], step.args[9]);
            } catch (ClassCastException ignored) {
            } try {
                OneParamSupplier supplier = (OneParamSupplier) step.reference;
                try {
                    OneParamConsumer consumer = (OneParamConsumer) step.condition;
                    consumer.accept(supplier.get(step.args[0]));
                } catch (ClassCastException ignored) {
                } catch (AssertionFailedError failed) {
                    handleFailedAssertion(failed);
                } try {
                    TwoParamConsumer consumer = (TwoParamConsumer) step.condition;
                    consumer.accept(step.expected, supplier.get(step.args[0]));
                } catch (ClassCastException ignored) {
                } catch (AssertionFailedError failed) {
                    handleFailedAssertion(failed);
                }
            } catch (ClassCastException ignored) {
            } try {
                TwoParamSupplier supplier = (TwoParamSupplier) step.reference;
                try {
                    OneParamConsumer consumer = (OneParamConsumer) step.condition;
                    consumer.accept(supplier.get(step.args[0], step.args[1]));
                } catch (ClassCastException ignored) {
                } catch (AssertionFailedError failed) {
                    handleFailedAssertion(failed);
                } try {
                    TwoParamConsumer consumer = (TwoParamConsumer) step.condition;
                    consumer.accept(step.expected, supplier.get(step.args[0], step.args[1]));
                } catch (ClassCastException ignored) {
                } catch (AssertionFailedError failed) {
                    handleFailedAssertion(failed);
                }
            } catch (ClassCastException ignored) {
            }
            // TODO: add the rest of the suppliers
        }

        private void printStepInformation(Step<?, ?> step) {
            // Get step definition/annotation value from state after aspect code has been executed
            // Not needed if the value has been set explicitly in the describeAs-method
            if (step.stepDefinition == null) {
                step.stepDefinition = state.get().getStepDefinition();
            }
            // Print out the step type and definition
            if (step.expected != null) {
                logger.log(step.stepType + " " + step.stepDefinition.replaceAll("\\{\\w*\\}", step.expected.toString()));
            } else {
                logger.log(step.stepType + " " + step.stepDefinition);
            }
            if (step.explanation != null) {
                logger.log("\tBecause: " + step.explanation);
            }
        }

        private void finishExecution(double executionTime) {
            logger.log("\n");
            HtmlReportBuilder.add(this, executionTime);
        }

        private void markStepFailed(Step<?, ?> step) {
            Step<?, ?> stepRef = new Step<>(step);
            stepRef.passed = Framework.state.get().getStepPassed();
            List<Step<?, ?>> updatedSteps = new ArrayList<>(this.steps);
            updatedSteps.set(this.steps.indexOf(step), stepRef);
            this.steps = updatedSteps;
        }

        private void handleFailedAssertion(AssertionFailedError error) {
            Framework.state.get().setStepPassed(false);
            throw error;
        }

        public static class ExampleBuilder {

            private String name;
            List<Step<?, ?>> steps;
            String narrative;
            String testClass;

            public ExampleBuilder() {
                this.steps = new ArrayList<>();
            }

            public ExampleBuilder name(String name) {
                this.name = name;
                return this;
            }

            public ExampleBuilder narrative(String narrative) {
                this.narrative = narrative;
                return this;
            }

            public ExampleBuilder steps(Step<?, ?>... steps) {
                this.steps = List.of(steps);
                return this;
            }

            public ExampleDefinition build() {
                this.testClass = state.get().getClassName();
                return new ExampleDefinition(this);
            }
        }
    }

    public static class Step<A, B> {

        private final StepType stepType;
        private String stepDefinition;
        private final A reference;
        private final Object[] args;
        private String explanation;
        private Object expected;
        private B condition;
        private double executionTime;
        private boolean passed = true;

        private Step(StepType stepType, A reference, Object... args) {
            this.stepType = stepType;
            this.reference = reference;
            this.args = args;
        }

        private Step(Step<A, B> another, Object... args) {
            this.stepType = another.stepType;
            this.stepDefinition = another.stepDefinition;
            this.reference = another.reference;
            this.explanation = another.explanation;
            this.expected = another.expected;
            this.condition = another.condition;
            this.args = args;
            // Update description after changing args
            describeAs(this.stepDefinition);
        }

        public StepType getType() {
            return this.stepType;
        }

        public String getStepDefinition() {
            return this.stepDefinition;
        }

        public boolean getPassed() {
            return this.passed;
        }

        public static <A> Step<?, ?> of(StepType stepType, A step, Object... args) {
            state.get().setPrev(stepType);
            return new Step<>(stepType, step, args);
        }

        public Step<?, ?> because(String explanation) {
            this.explanation = explanation;
            return this;
        }

        public Step<?, ?> describeAs(String stepDefinition) {
            if (this.args.length > 0) {
                for (Object arg : this.args) {
                    if (arg != null) {
                        stepDefinition = stepDefinition.replaceFirst(" \\{(.*?)}", " " + arg);
                    }
                }
            } if (expected != null) {
                stepDefinition = stepDefinition.replaceFirst(" \\[(.*?)]", " " + expected);
            }
            this.stepDefinition = stepDefinition;
            return this;
        }

        public Step<?, ?> satisfies(OneParamConsumer<?> condition) {
            if (!isSupplier(this.reference)) {
                throw new RuntimeException(NOT_A_SUPPLIER_ERROR);
            }
            this.condition = (B) condition;
            return this;
        }

        public <Q> Step<?, ?> satisfies(TwoParamConsumer<Q, Q> condition, Object expected) {
            if (!isSupplier(this.reference)) {
                throw new RuntimeException(NOT_A_SUPPLIER_ERROR);
            }
            this.condition = (B) condition;
            if (expected == null) {
                // Explicitly set null is different from not having an expected return value
                expected = "null";
            }
            this.expected = expected;
            // Update description if needed after adding an expected value
            // Whether this is needed depends on the order of calling satisfies and describeAs
            if (this.stepDefinition != null) {
                describeAs(this.stepDefinition);
            }
            return this;
        }

        // Can be used to reuse steps with different args
        public Step<?, ?> withArgs(Object... args) {
            return new Step<>(this, args);
        }

        public double getExecutionTime() {
            return this.executionTime;
        }

        public void setExecutionTime(double executionTime) {
            this.executionTime = executionTime;
        }

        private static final String NOT_A_SUPPLIER_ERROR =
                "This can only be used for methods with a return value!";
    }



    /** GIVENS, WHENS, THENS, ANDS **/

    // No params
    public static Step<?, ?> given(Runnable r) {
        return Step.of(DefaultStepType.GIVEN, r);
    }

    public static Step<?, ?> when(Runnable r) {
        return Step.of(DefaultStepType.WHEN, r);
    }

    public static Step<?, ?> then(Runnable r) {
        return Step.of(DefaultStepType.THEN, r);
    }

    public static Step<?, ?> and(Runnable r) {
        return Step.of(state.get().getPrev(), r);
    }

    // Suppliers only supported for then + and
    public static Step<?, ?> then(Supplier<?> q) {
        return Step.of(DefaultStepType.THEN, q);
    }

    public static Step<?, ?> and(Supplier<?> q) {
        return Step.of(state.get().getPrev(), q);
    }

    // One param
    public static <Q> Step<?, ?> given(OneParamConsumer<Q> c, Q q) {
        return Step.of(DefaultStepType.GIVEN, c, q);
    }

    public static <Q> Step<?, ?> when(OneParamConsumer<Q> c, Q q) {
        return Step.of(DefaultStepType.WHEN, c, q);
    }

    public static <Q> Step<?, ?> then(OneParamConsumer<Q> c, Q q) {
        return Step.of(DefaultStepType.THEN, c, q);
    }

    public static <Q> Step<?, ?> and(OneParamConsumer<Q> c, Q q) {
        return Step.of(state.get().getPrev(), c, q);
    }

    public static <Q> Step<?, ?> then(OneParamSupplier<Q> c, Q q) {
        return Step.of(DefaultStepType.THEN, c, q);
    }

    public static <Q> Step<?, ?> and(OneParamSupplier<Q> c, Q q) {
        return Step.of(state.get().getPrev(), c, q);
    }

    // Two params
    public static <Q, R> Step<?, ?> given(TwoParamConsumer<Q, R> c, Q q, R r) {
        return Step.of(DefaultStepType.GIVEN, c, q, r);
    }

    public static <Q, R> Step<?, ?> when(TwoParamConsumer<Q, R> c, Q q, R r) {
        return Step.of(DefaultStepType.WHEN, c, q, r);
    }

    public static <Q, R> Step<?, ?> then(TwoParamConsumer<Q, R> c, Q q, R r) {
        return Step.of(DefaultStepType.THEN, c, q, r);
    }

    public static <Q, R> Step<?, ?> and(TwoParamConsumer<Q, R> c, Q q, R r) {
        return Step.of(state.get().getPrev(), c, q, r);
    }

    public static <Q, R> Step<?, ?> then(TwoParamSupplier<Q, R> c, Q q, R r) {
        return Step.of(DefaultStepType.THEN, c, q, r);
    }

    public static <Q, R> Step<?, ?> and(TwoParamSupplier<Q, R> c, Q q, R r) {
        return Step.of(state.get().getPrev(), c, q, r);
    }

    // Three params
    public static <Q, R, S> Step<?, ?> given(ThreeParamConsumer<Q, R, S> c, Q q, R r, S s) {
        return Step.of(DefaultStepType.GIVEN, c, q, r, s);
    }

    public static <Q, R, S> Step<?, ?> when(ThreeParamConsumer<Q, R, S> c, Q q, R r, S s) {
        return Step.of(DefaultStepType.WHEN, c, q, r, s);
    }

    public static <Q, R, S> Step<?, ?> then(ThreeParamConsumer<Q, R, S> c, Q q, R r, S s) {
        return Step.of(DefaultStepType.THEN, c, q, r, s);
    }

    public static <Q, R, S> Step<?, ?> and(ThreeParamConsumer<Q, R, S> c, Q q, R r, S s) {
        return Step.of(state.get().getPrev(), c, q, r, s);
    }

    public static <Q, R, S> Step<?, ?> then(ThreeParamSupplier<Q, R, S> c, Q q, R r, S s) {
        return Step.of(DefaultStepType.THEN, c, q, r, s);
    }

    public static <Q, R, S> Step<?, ?> and(ThreeParamSupplier<Q, R, S> c, Q q, R r, S s) {
        return Step.of(state.get().getPrev(), c, q, r, s);
    }

    // Four params
    public static <Q, R, S, T> Step<?, ?> given(FourParamConsumer<Q, R, S, T> c, Q q, R r, S s, T t) {
        return Step.of(DefaultStepType.GIVEN, c, q, r, s, t);
    }

    public static <Q, R, S, T> Step<?, ?> when(FourParamConsumer<Q, R, S, T> c, Q q, R r, S s, T t) {
        return Step.of(DefaultStepType.WHEN, c, q, r, s, t);
    }

    public static <Q, R, S, T> Step<?, ?> then(FourParamConsumer<Q, R, S, T> c, Q q, R r, S s, T t) {
        return Step.of(DefaultStepType.THEN, c, q, r, s, t);
    }

    public static <Q, R, S, T> Step<?, ?> and(FourParamConsumer<Q, R, S, T> c, Q q, R r, S s, T t) {
        return Step.of(state.get().getPrev(), c, q, r, s, t);
    }

    public static <Q, R, S, T> Step<?, ?> then(FourParamSupplier<Q, R, S, T> c, Q q, R r, S s, T t) {
        return Step.of(DefaultStepType.THEN, c, q, r, s, t);
    }

    public static <Q, R, S, T> Step<?, ?> and(FourParamSupplier<Q, R, S, T> c, Q q, R r, S s, T t) {
        return Step.of(state.get().getPrev(), c, q, r, s, t);
    }

    // Five params
    public static <Q, R, S, T, U> Step<?, ?> given(FiveParamConsumer<Q, R, S, T, U> c, Q q, R r, S s, T t, U u) {
        return Step.of(DefaultStepType.GIVEN, c, q, r, s, t, u);
    }

    public static <Q, R, S, T, U> Step<?, ?> when(FiveParamConsumer<Q, R, S, T, U> c, Q q, R r, S s, T t, U u) {
        return Step.of(DefaultStepType.WHEN, c, q, r, s, t, u);
    }

    public static <Q, R, S, T, U> Step<?, ?> then(FiveParamConsumer<Q, R, S, T, U> c, Q q, R r, S s, T t, U u) {
        return Step.of(DefaultStepType.THEN, c, q, r, s, t, u);
    }

    public static <Q, R, S, T, U> Step<?, ?> and(FiveParamConsumer<Q, R, S, T, U> c, Q q, R r, S s, T t, U u) {
        return Step.of(state.get().getPrev(), c, q, r, s, t, u);
    }

    public static <Q, R, S, T, U> Step<?, ?> then(FiveParamSupplier<Q, R, S, T, U> c, Q q, R r, S s, T t, U u) {
        return Step.of(DefaultStepType.THEN, c, q, r, s, t, u);
    }

    public static <Q, R, S, T, U> Step<?, ?> and(FiveParamSupplier<Q, R, S, T, U> c, Q q, R r, S s, T t, U u) {
        return Step.of(state.get().getPrev(), c, q, r, s, t, u);
    }

    // Six params
    public static <Q, R, S, T, U, V> Step<?, ?> given(SixParamConsumer<Q, R, S, T, U, V> c, Q q, R r, S s, T t, U u, V v) {
        return Step.of(DefaultStepType.GIVEN, c, q, r, s, t, u, v);
    }

    public static <Q, R, S, T, U, V> Step<?, ?> when(SixParamConsumer<Q, R, S, T, U, V> c, Q q, R r, S s, T t, U u, V v) {
        return Step.of(DefaultStepType.WHEN, c, q, r, s, t, u, v);
    }

    public static <Q, R, S, T, U, V> Step<?, ?> then(SixParamConsumer<Q, R, S, T, U, V> c, Q q, R r, S s, T t, U u, V v) {
        return Step.of(DefaultStepType.THEN, c, q, r, s, t, u, v);
    }

    public static <Q, R, S, T, U, V> Step<?, ?> and(SixParamConsumer<Q, R, S, T, U, V> c, Q q, R r, S s, T t, U u, V v) {
        return Step.of(state.get().getPrev(), c, q, r, s, t, u, v);
    }

    public static <Q, R, S, T, U, V> Step<?, ?> then(SixParamSupplier<Q, R, S, T, U, V> c, Q q, R r, S s, T t, U u, V v) {
        return Step.of(DefaultStepType.THEN, c, q, r, s, t, u, v);
    }

    public static <Q, R, S, T, U, V> Step<?, ?> and(SixParamSupplier<Q, R, S, T, U, V> c, Q q, R r, S s, T t, U u, V v) {
        return Step.of(state.get().getPrev(), c, q, r, s, t, u, v);
    }

    // Seven params
    public static <Q, R, S, T, U, V, W> Step<?, ?> given(SevenParamConsumer<Q, R, S, T, U, V, W> c, Q q, R r, S s, T t, U u, V v, W w) {
        return Step.of(DefaultStepType.GIVEN, c, q, r, s, t, u, v, w);
    }

    public static <Q, R, S, T, U, V, W> Step<?, ?> when(SevenParamConsumer<Q, R, S, T, U, V, W> c, Q q, R r, S s, T t, U u, V v, W w) {
        return Step.of(DefaultStepType.WHEN, c, q, r, s, t, u, v, w);
    }

    public static <Q, R, S, T, U, V, W> Step<?, ?> then(SevenParamConsumer<Q, R, S, T, U, V, W> c, Q q, R r, S s, T t, U u, V v, W w) {
        return Step.of(DefaultStepType.THEN, c, q, r, s, t, u, v, w);
    }

    public static <Q, R, S, T, U, V, W> Step<?, ?> and(SevenParamConsumer<Q, R, S, T, U, V, W> c, Q q, R r, S s, T t, U u, V v, W w) {
        return Step.of(state.get().getPrev(), c, q, r, s, t, u, v, w);
    }

    public static <Q, R, S, T, U, V, W> Step<?, ?> then(SevenParamSupplier<Q, R, S, T, U, V, W> c, Q q, R r, S s, T t, U u, V v, W w) {
        return Step.of(DefaultStepType.THEN, c, q, r, s, t, u, v, w);
    }

    public static <Q, R, S, T, U, V, W> Step<?, ?> and(SevenParamSupplier<Q, R, S, T, U, V, W> c, Q q, R r, S s, T t, U u, V v, W w) {
        return Step.of(state.get().getPrev(), c, q, r, s, t, u, v, w);
    }

    // Eight params
    public static <Q, R, S, T, U, V, W, X> Step<?, ?> given(EightParamConsumer<Q, R, S, T, U, V, W, X> c, Q q, R r, S s, T t, U u, V v, W w, X x) {
        return Step.of(DefaultStepType.GIVEN, c, q, r, s, t, u, v, w, x);
    }

    public static <Q, R, S, T, U, V, W, X> Step<?, ?> when(EightParamConsumer<Q, R, S, T, U, V, W, X> c, Q q, R r, S s, T t, U u, V v, W w, X x) {
        return Step.of(DefaultStepType.WHEN, c, q, r, s, t, u, v, w, x);
    }

    public static <Q, R, S, T, U, V, W, X> Step<?, ?> then(EightParamConsumer<Q, R, S, T, U, V, W, X> c, Q q, R r, S s, T t, U u, V v, W w, X x) {
        return Step.of(DefaultStepType.THEN, c, q, r, s, t, u, v, w, x);
    }

    public static <Q, R, S, T, U, V, W, X> Step<?, ?> and(EightParamConsumer<Q, R, S, T, U, V, W, X> c, Q q, R r, S s, T t, U u, V v, W w, X x) {
        return Step.of(state.get().getPrev(), c, q, r, s, t, u, v, w, x);
    }

    public static <Q, R, S, T, U, V, W, X> Step<?, ?> then(EightParamSupplier<Q, R, S, T, U, V, W, X> c, Q q, R r, S s, T t, U u, V v, W w, X x) {
        return Step.of(DefaultStepType.THEN, c, q, r, s, t, u, v, w, x);
    }

    public static <Q, R, S, T, U, V, W, X> Step<?, ?> and(EightParamSupplier<Q, R, S, T, U, V, W, X> c, Q q, R r, S s, T t, U u, V v, W w, X x) {
        return Step.of(state.get().getPrev(), c, q, r, s, t, u, v, w, x);
    }

    // Nine params
    public static <Q, R, S, T, U, V, W, X, Y> Step<?, ?> given(NineParamConsumer<Q, R, S, T, U, V, W, X, Y> c, Q q, R r, S s, T t, U u, V v, W w, X x, Y y) {
        return Step.of(DefaultStepType.GIVEN, c, q, r, s, t, u, v, w, x, y);
    }

    public static <Q, R, S, T, U, V, W, X, Y> Step<?, ?> when(NineParamConsumer<Q, R, S, T, U, V, W, X, Y> c, Q q, R r, S s, T t, U u, V v, W w, X x, Y y) {
        return Step.of(DefaultStepType.WHEN, c, q, r, s, t, u, v, w, x, y);
    }

    public static <Q, R, S, T, U, V, W, X, Y> Step<?, ?> then(NineParamConsumer<Q, R, S, T, U, V, W, X, Y> c, Q q, R r, S s, T t, U u, V v, W w, X x, Y y) {
        return Step.of(DefaultStepType.THEN, c, q, r, s, t, u, v, w, x, y);
    }

    public static <Q, R, S, T, U, V, W, X, Y> Step<?, ?> and(NineParamConsumer<Q, R, S, T, U, V, W, X, Y> c, Q q, R r, S s, T t, U u, V v, W w, X x, Y y) {
        return Step.of(state.get().getPrev(), c, q, r, s, t, u, v, w, x, y);
    }

    public static <Q, R, S, T, U, V, W, X, Y> Step<?, ?> then(NineParamSupplier<Q, R, S, T, U, V, W, X, Y> c, Q q, R r, S s, T t, U u, V v, W w, X x, Y y) {
        return Step.of(DefaultStepType.THEN, c, q, r, s, t, u, v, w, x, y);
    }

    public static <Q, R, S, T, U, V, W, X, Y> Step<?, ?> and(NineParamSupplier<Q, R, S, T, U, V, W, X, Y> c, Q q, R r, S s, T t, U u, V v, W w, X x, Y y) {
        return Step.of(state.get().getPrev(), c, q, r, s, t, u, v, w, x, y);
    }

    // Ten params
    public static <Q, R, S, T, U, V, W, X, Y, Z> Step<?, ?> given(TenParamConsumer<Q, R, S, T, U, V, W, X, Y, Z> c, Q q, R r, S s, T t, U u, V v, W w, X x, Y y, Z z) {
        return Step.of(DefaultStepType.GIVEN, c, q, r, s, t, u, v, w, x, y, z);
    }

    public static <Q, R, S, T, U, V, W, X, Y, Z> Step<?, ?> when(TenParamConsumer<Q, R, S, T, U, V, W, X, Y, Z> c, Q q, R r, S s, T t, U u, V v, W w, X x, Y y, Z z) {
        return Step.of(DefaultStepType.WHEN, c, q, r, s, t, u, v, w, x, y, z);
    }

    public static <Q, R, S, T, U, V, W, X, Y, Z> Step<?, ?> then(TenParamConsumer<Q, R, S, T, U, V, W, X, Y, Z> c, Q q, R r, S s, T t, U u, V v, W w, X x, Y y, Z z) {
        return Step.of(DefaultStepType.THEN, c, q, r, s, t, u, v, w, x, y, z);
    }

    public static <Q, R, S, T, U, V, W, X, Y, Z> Step<?, ?> and(TenParamConsumer<Q, R, S, T, U, V, W, X, Y, Z> c, Q q, R r, S s, T t, U u, V v, W w, X x, Y y, Z z) {
        return Step.of(state.get().getPrev(), c, q, r, s, t, u, v, w, x, y, z);
    }

    public static <Q, R, S, T, U, V, W, X, Y, Z> Step<?, ?> then(TenParamSupplier<Q, R, S, T, U, V, W, X, Y, Z> c, Q q, R r, S s, T t, U u, V v, W w, X x, Y y, Z z) {
        return Step.of(DefaultStepType.THEN, c, q, r, s, t, u, v, w, x, y, z);
    }

    public static <Q, R, S, T, U, V, W, X, Y, Z> Step<?, ?> and(TenParamSupplier<Q, R, S, T, U, V, W, X, Y, Z> c, Q q, R r, S s, T t, U u, V v, W w, X x, Y y, Z z) {
        return Step.of(state.get().getPrev(), c, q, r, s, t, u, v, w, x, y, z);
    }
}
