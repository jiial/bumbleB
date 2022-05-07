package com.jiial.bumbleB.examples.extensions;

import com.jiial.bumbleB.framework.Framework;
import com.jiial.bumbleB.framework.Utils.*;

public class ExtendedFramework extends Framework {

    // No params
    public static Step<?, ?> after(Runnable r) {
        return Step.of(ExtendedStepType.AFTER, r);
    }

    // One param
    public static <U> Step<?, ?> after(OneParamConsumer<U> c, U arg2) {
        return Step.of(ExtendedStepType.AFTER, c, arg2);
    }

    // Two params
    public static <U, V> Step<?, ?> after(TwoParamConsumer<U, V> c, U arg2, V arg3) {
        return Step.of(ExtendedStepType.AFTER, c, arg2, arg3);
    }

    enum ExtendedStepType implements StepType {
        AFTER("After");

        private final String definition;

        ExtendedStepType(String definition) {
            this.definition = definition;
        }

        @Override
        public String toString() {
            return this.definition;
        }
    }
}
