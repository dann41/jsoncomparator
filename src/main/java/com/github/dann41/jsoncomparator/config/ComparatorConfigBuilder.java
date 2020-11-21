package com.github.dann41.jsoncomparator.config;

public final class ComparatorConfigBuilder {
    private boolean recursive;
    private boolean arrayIncluded;
    private boolean concatenateFields;
    private String fieldSeparator;

    private ComparatorConfigBuilder() {
    }

    public static ComparatorConfigBuilder aComparatorConfig() {
        return new ComparatorConfigBuilder();
    }

    public ComparatorConfigBuilder withRecursive(boolean recursive) {
        this.recursive = recursive;
        return this;
    }

    public ComparatorConfigBuilder withArrayIncluded(boolean arrayIncluded) {
        this.arrayIncluded = arrayIncluded;
        return this;
    }

    public ComparatorConfigBuilder withConcatenateFields(boolean concatenateFields) {
        this.concatenateFields = concatenateFields;
        return this;
    }

    public ComparatorConfigBuilder withFieldSeparator(String fieldSeparator) {
        this.fieldSeparator = fieldSeparator;
        return this;
    }

    public ComparatorConfig build() {
        return new ComparatorConfig(recursive, arrayIncluded, concatenateFields, fieldSeparator);
    }
}
