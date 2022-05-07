package com.jiial.bumbleB.assertions;

import com.jiial.bumbleB.framework.Utils;

/**
 * Extension of JUnit's assertion library to support using boolean asserts as method references in bumbleB's satisfies-
 * methods {@link com.jiial.bumbleB.framework.Framework.Step#satisfies(Utils.OneParamConsumer)} and
 * {@link com.jiial.bumbleB.framework.Framework.Step#satisfies(Utils.TwoParamConsumer, Object)}
 */
public class Assertions extends org.junit.jupiter.api.Assertions {

    public static void assertTrue(Object condition) {
        if (!Boolean.class.isAssignableFrom(condition.getClass())) {
            throw new RuntimeException("Cannot assert false for a non-boolean condition!");
        }
        org.junit.jupiter.api.Assertions.assertTrue((Boolean) condition);
    }

    public static void assertFalse(Object condition) {
        if (!Boolean.class.isAssignableFrom(condition.getClass())) {
            throw new RuntimeException("Cannot assert false for a non-boolean condition!");
        }
        org.junit.jupiter.api.Assertions.assertFalse((Boolean) condition);
    }
}
