package com.jiial.bumbleB.framework;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;


public class Framework {

    public static final ThreadLocal<StateHolder> state = ThreadLocal.withInitial(StateHolder::new);

    public static class ExampleDefinition {
        String name;
        List<Step<?>> steps;
        String narrative;

        public ExampleDefinition(ExampleBuilder builder) {
            this.name = builder.name;
            this.narrative = builder.narrative;
            this.steps = builder.steps;
        }

        public List<Step<?>> getSteps() {
            return steps;
        }

        public void run() {
            for (Step<?> step : steps) {
                // Set args for StepAspect
                state.get().setArgs(step.args);
                // Cast the method reference to correct type and execute the step
                castAndExecute(step);
                // Get step definition/annotation value from state after aspect code has been executed
                step.stepDefinition = state.get().getStepDefinition();
                // Print out the step type and definition
                if (step.expectation != null) {
                    System.out.println(step.stepType + " " + step.stepDefinition + " " + step.expectation);
                } else {
                    System.out.println(step.stepType + " " + step.stepDefinition);
                }
            }
            System.out.println("\n");
        }

        private void castAndExecute(Step<?> step) {
            try {
                Runnable runnable = (Runnable) step.reference;
                runnable.run();
            } catch (Exception ignored) {
            } try {
                Supplier<?> supplier = (Supplier<?>) step.reference;
                if (step.expectation != null) {
                    assertThat(supplier.get()).isEqualTo(step.expectation);
                }
            } catch (Exception ignored) {
            } try {
                OneParamConsumer consumer = (OneParamConsumer) step.reference;
                consumer.accept(step.args[0]);
            } catch (Exception ignored) {
            } try {
                TwoParamConsumer consumer = (TwoParamConsumer) step.reference;
                consumer.accept(step.args[0], step.args[1]);
            } catch (Exception ignored) {
            }
        }

        public static class ExampleBuilder {

            private String name;
            List<Step<?>> steps;

            String narrative;

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

            public ExampleBuilder steps(Step<?>... steps) {
                this.steps = List.of(steps);
                return this;
            }

            public ExampleDefinition build() {
                return new ExampleDefinition(this);
            }
        }
    }

    public static class Step<T> {

        private final StepType stepType;
        private String stepDefinition;
        private final T reference;
        private final Object[] args;
        private String explanation;
        private Object expectation;

        private Step(StepType stepType, T reference, Object... args) {
            this.stepType = stepType;
            this.reference = reference;
            this.args = args;
        }

        public StepType type() {
            return this.stepType;
        }

        public static <T> Step<?> of(StepType stepType, T step, Object... args) {
            state.get().setPrev(stepType);
            return new Step<>(stepType, step, args);
        }

        public Step<?> because(String explanation) {
            this.explanation = explanation;
            return this;
        }

        public Step<?> isEqualTo(Object object) {
            this.expectation = object;
            return this;
        }
    }


    protected enum DefaultStepType implements StepType  {
        GIVEN("Given"),
        WHEN("When"),
        THEN("Then");

        private final String description;

        DefaultStepType(String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return this.description;
        }
    }

    protected interface StepType {
        String toString();
    }


    /** GIVENS, WHENS, THENS, ANDS **/

    // No params
    public static Step<?> given(Runnable r) {
        return Step.of(DefaultStepType.GIVEN, r);
    }

    public static Step<?> when(Runnable r) {
        return Step.of(DefaultStepType.WHEN, r);
    }

    public static Step<?> then(Runnable r) {
        return Step.of(DefaultStepType.THEN, r);
    }

    public static Step<?> and(Runnable r) {
        return Step.of(state.get().getPrev(), r);
    }

    // No param supplier (test)
    public static <U> Step<?> then(Supplier<U> u) {
        return Step.of(DefaultStepType.THEN, u);
    }

    // One param
    public static <U> Step<?> given(OneParamConsumer<U> c, U arg2) {
        return Step.of(DefaultStepType.GIVEN, c, arg2);
    }

    public static <U> Step<?> when(OneParamConsumer<U> c, U arg2) {
        return Step.of(DefaultStepType.WHEN, c, arg2);
    }

    public static <U> Step<?> then(OneParamConsumer<U> c, U arg2) {
        return Step.of(DefaultStepType.THEN, c, arg2);
    }

    public static <U> Step<?> and(OneParamConsumer<U> c, U arg2) {
        return Step.of(state.get().getPrev(), c, arg2);
    }

    // Two params
    public static <U, V> Step<?> given(TwoParamConsumer<U, V> c, U arg2, V arg3) {
        return Step.of(DefaultStepType.GIVEN, c, arg2, arg3);
    }

    public static <U, V> Step<?> when(TwoParamConsumer<U, V> c, U arg2, V arg3) {
        return Step.of(DefaultStepType.WHEN, c, arg2, arg3);
    }

    public static <U, V> Step<?> then(TwoParamConsumer<U, V> c, U arg2, V arg3) {
        return Step.of(DefaultStepType.THEN, c, arg2, arg3);
    }

    public static <U, V> Step<?> and(TwoParamConsumer<U, V> c, U arg2, V arg3) {
        return Step.of(state.get().getPrev(), c, arg2, arg3);
    }

    // ------------------------------------------------------------------------------------------------------------
    // Consumers used by givens/whens/thens

    public interface OneParamConsumer<U> {
        void accept(U u);
    }

    public interface TwoParamConsumer<U, V> {
        void accept(U u, V v);
    }

    public interface ThreeParamConsumer<U, V, W> {
        void accept(U u, V v, W w);
    }

    // etc....
}
