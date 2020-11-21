package com.github.dann41.jsoncomparator;

import com.github.dann41.jsoncomparator.config.ComparatorConfig;
import com.github.dann41.jsoncomparator.result.ComparisonResult;
import com.github.dann41.jsoncomparator.result.Difference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasKey;

public class JsonComparatorTest {

    private JsonComparator jsonComparator;

    @BeforeEach
    void setUp() {
        jsonComparator = new RecursiveJsonComparator();
    }


    @Test
    void givenEmptyStringsWhenComparingThenReturnEmptyComparisonResult() {
        ComparisonResult result = jsonComparator.compare("", "");

        assertThat(result, is(notNullValue()));
        assertThat(result.getAllDiffs(), is(Collections.emptyMap()));
    }

    @Test
    void givenEmptyJSONWhenComparingThenReturnEmptyComparisonResult() {
        ComparisonResult result = jsonComparator.compare("{}", "{}");

        assertThat(result, is(notNullValue()));
        assertThat(result.getAllDiffs(), is(Collections.emptyMap()));
    }

    @Test
    void givenNewJSONWithNewPropertyWhenComparingThenReturnResultWithPropertyAdded() {
        ComparisonResult result = jsonComparator.compare("{}", "{\"hello\":\"world\"}");

        assertThat(result, is(notNullValue()));
        assertThat(result.getAllDiffs().size(), is(1));
        assertThat(result.getAdded().size(), is(1));
        assertThat(result.getRemoved().size(), is(0));
        assertThat(result.getUpdated().size(), is(0));
        assertThat(result.getAdded(), hasEntry("hello", Difference.of(null, "world")));
    }

    @Test
    void givenNewJSONWithoutOldPropertyWhenComparingThenReturnResultWithPropertyRemoved() {
        ComparisonResult result = jsonComparator.compare("{\"hello\":\"world\"}", "{}");

        assertThat(result, is(notNullValue()));
        assertThat(result.getAllDiffs().size(), is(1));
        assertThat(result.getAdded().size(), is(0));
        assertThat(result.getRemoved().size(), is(1));
        assertThat(result.getUpdated().size(), is(0));
        assertThat(result.getRemoved(), hasEntry("hello", Difference.of("world", null)));
    }

    @Test
    void givenNewJSONWithValueUpdatedPropertyWhenComparingThenReturnResultWithPropertyUpdated() {
        ComparisonResult result = jsonComparator.compare("{\"hello\":\"world\"}", "{\"hello\":1}");

        assertThat(result, is(notNullValue()));
        assertThat(result.getAllDiffs().size(), is(1));
        assertThat(result.getAdded().size(), is(0));
        assertThat(result.getRemoved().size(), is(0));
        assertThat(result.getUpdated().size(), is(1));
        assertThat(result.getUpdated(), hasEntry("hello", Difference.of("world", 1)));
    }

    @Test
    void givenNestedJSONWhenComparingThenReturnResultIncludingNestedDifferences() {
        ComparisonResult result = jsonComparator.compare(
                "{\"greetings\":{\"Spanish\":\"hola\"}}",
                "{\"greetings\":{\"Spanish\":\"hola!\",\"English\":\"hello\"}}");

        assertThat(result, is(notNullValue()));
        assertThat(result.getAllDiffs().size(), is(2));
        assertThat(result.getAdded().size(), is(1));
        assertThat(result.getRemoved().size(), is(0));
        assertThat(result.getUpdated().size(), is(1));
        assertThat(result.getAdded(), hasEntry("greetings.English", Difference.of(null, "hello")));
        assertThat(result.getUpdated(), hasEntry("greetings.Spanish", Difference.of("hola", "hola!")));
    }

    @Test
    void givenNestedJSONWhenComparingWithoutNestedThenReturnResultIncludingTopLevelDifferences() {
        JsonComparator jsonComparator = new RecursiveJsonComparator(
                ComparatorConfig.aComparatorConfig().withRecursive(false).build()
        );
        ComparisonResult result = jsonComparator.compare(
                "{\"greetings\":{\"Spanish\":\"hola\"}}",
                "{\"greetings\":{\"Spanish\":\"hola!\",\"English\":\"hello\"}}");

        assertThat(result, is(notNullValue()));
        assertThat(result.getAllDiffs().size(), is(1));
        assertThat(result.getAdded().size(), is(0));
        assertThat(result.getRemoved().size(), is(0));
        assertThat(result.getUpdated().size(), is(1));
        assertThat(result.getUpdated(), hasKey("greetings"));
    }

}
