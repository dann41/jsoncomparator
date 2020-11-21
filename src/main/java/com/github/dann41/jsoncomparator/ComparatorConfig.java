package com.github.dann41.jsoncomparator;

public class ComparatorConfig {

  private static final String DEFAULT_SEPARATOR = ".";

  private final boolean recursive;
  private final boolean arrayIncluded;
  private final boolean concatenateFields;
  private final String fieldSeparator;

  public ComparatorConfig(boolean recursive, boolean arrayIncluded, boolean concatenateFields, String fieldSeparator) {
    this.recursive = recursive;
    this.arrayIncluded = arrayIncluded;
    this.concatenateFields = concatenateFields;
    this.fieldSeparator = fieldSeparator;
  }

  public boolean isRecursive() {
    return recursive;
  }

  public boolean isArrayIncluded() {
    return arrayIncluded;
  }

  public boolean isConcatenateFields() {
    return concatenateFields;
  }

  public String getFieldSeparator() {
    return fieldSeparator;
  }


  public static ComparatorConfigBuilder aComparatorConfig() {
    return new ComparatorConfigBuilder();
  }

  final static class ComparatorConfigBuilder {
    private boolean recursive = true;
    private boolean arrayIncluded = false;
    private boolean concatenateFields = true;
    private String fieldSeparator = DEFAULT_SEPARATOR;

    private ComparatorConfigBuilder() {
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

}
