package com.github.dann41.jsoncomparator.result;

import java.util.Objects;

public class Difference<T> {

    private final T from;
    private final T to;

    public static <T> Difference<T> of(T from, T to) {
        return new Difference<>(from, to);
    }

    private Difference(T from, T to) {
        this.from = from;
        this.to = to;
    }

    public T getFrom() {
        return from;
    }

    public T getTo() {
        return to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Difference<?> that = (Difference<?>) o;
        return Objects.equals(from, that.from) &&
                Objects.equals(to, that.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }


}
