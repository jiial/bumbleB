package com.jiial.bumbleB.framework;

import java.util.function.Supplier;

/**
 * Utility classes and methods used by the {@link Framework}.
 */
public class Utils {

    public static boolean isSupplier(Object object) {
        return object instanceof Supplier | object instanceof OneParamSupplier<?> | object instanceof
                TwoParamSupplier<?,?> | object instanceof ThreeParamSupplier<?,?,?> | object instanceof
                FourParamSupplier<?,?,?,?> | object instanceof FiveParamSupplier<?,?,?,?,?> | object instanceof
                SixParamSupplier<?,?,?,?,?,?> | object instanceof SevenParamSupplier<?,?,?,?,?,?,?> | object instanceof
                EightParamSupplier<?,?,?,?,?,?,?,?> | object instanceof NineParamSupplier<?,?,?,?,?,?,?,?,?> | object
                instanceof TenParamSupplier<?,?,?,?,?,?,?,?,?,?>;

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

    public interface StepType {
        String toString();
    }

    // ------------------------------------------------------------------------------------------------------------
    // Consumers used by givens/whens/thens

    public interface OneParamConsumer<Q> {
        void accept(Q q);
    }

    public interface TwoParamConsumer<Q, R> {
        void accept(Q q, R r);
    }

    public interface ThreeParamConsumer<Q, R, S> {
        void accept(Q q, R r, S s);
    }

    public interface FourParamConsumer<Q, R, S, T> {
        void accept(Q q, R r, S s, T t);
    }

    public interface FiveParamConsumer<Q, R, S, T, U> {
        void accept(Q q, R r, S s, T t, U u);
    }

    public interface SixParamConsumer<Q, R, S, T, U, V> {
        void accept(Q q, R r, S s, T t, U u, V v);
    }

    public interface SevenParamConsumer<Q, R, S, T, U, V, W> {
        void accept(Q q, R r, S s, T t, U u, V v, W w);
    }

    public interface EightParamConsumer<Q, R, S, T, U, V, W, X> {
        void accept(Q q, R r, S s, T t, U u, V v, W w, X x);
    }

    public interface NineParamConsumer<Q, R, S, T, U, V, W, X, Y> {
        void accept(Q q, R r, S s, T t, U u, V v, W w, X x, Y y);
    }

    public interface TenParamConsumer<Q, R, S, T, U, V, W, X, Y, Z> {
        void accept(Q q, R r, S s, T t, U u, V v, W w, X x, Y y, Z z);
    }

    // ------------------------------------------------------------------------------------------------------------
    // Suppliers used by thens/ands

    public interface OneParamSupplier<Q> {
        Object get(Q q);
    }

    public interface TwoParamSupplier<Q, R> {
        Object get(Q q, R r);
    }

    public interface ThreeParamSupplier<Q, R, S> {
        Object get(Q q, R r, S s);
    }

    public interface FourParamSupplier<Q, R, S, T> {
        Object get(Q q, R r, S s, T t);
    }

    public interface FiveParamSupplier<Q, R, S, T, U> {
        Object get(Q q, R r, S s, T t, U u);
    }

    public interface SixParamSupplier<Q, R, S, T, U, V> {
        Object get(Q q, R r, S s, T t, U u, V v);
    }

    public interface SevenParamSupplier<Q, R, S, T, U, V, W> {
        Object get(Q q, R r, S s, T t, U u, V v, W w);
    }

    public interface EightParamSupplier<Q, R, S, T, U, V, W, X> {
        Object get(Q q, R r, S s, T t, U u, V v, W w, X x);
    }

    public interface NineParamSupplier<Q, R, S, T, U, V, W, X, Y> {
        Object get(Q q, R r, S s, T t, U u, V v, W w, X x, Y y);
    }

    public interface TenParamSupplier<Q, R, S, T, U, V, W, X, Y, Z> {
        Object get(Q q, R r, S s, T t, U u, V v, W w, X x, Y y, Z z);
    }
}
