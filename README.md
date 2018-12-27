# jsoncomparator
Java library to compare JSON objects

## How to use

### JSON as String comparison
```
JSONComparator comparator = new JSONComparator();
ComparisonResult result = comparator.compare("{}", "{}");
```

### JsonNode comparison
```
JsonNode jsonNode1 = ...
JsonNode jsonNode2 = ...
ComparisonResult result = comparator.compare(jsonNode1, jsonNode2);
```

### Configure the comparison

* Recursive: to compare both jsons in depth (vs top level)
* Array comparison: to compare arrays index by index (vs as a single object)
* Field key as path: when recursivity is enabled, there's the option to concatenate all fields from top to bottom with a custom separator

```
ComparatorConfig config = ComparatorConfig.aComparatorConfig()
    .withRecursive(true)
    .withArrayIncluded(false)
    .withConcatenateFields(true)
    .withFieldSeparator(".")
    .build();

JSONComparator comparator = new JSONComparator();
ComparisonResult result = comparator.compare("{}", "{}", config);    
```
## The result
The comparison returns a ComparisonResult object containing all the differences found according to the provided (or default) ComparisonConfig.

The results are classified in three categories, always based on the difference of the second JSON compared to the first one:
* Fields added to the second json that weren't in the first one
* Fields removed from the second json that were present in the first one
* Fields that have changed the value between the first and the second json

By default, the differences between 

```
{"hello": "hola", "bye": "adios"}
```

and 
```
{"hello": "hola!", "good morning": "buenos días", "complex": { "field": 1234 }}
```

would be (in json pseudocode)
```
{
  "added": {
    "good morning": {
      "from": null,
      "to": "buenos días"
    },
    "complex.field": {
      "from": null,
      "to": 1234
    }
  },
  "updated": {
    "hello": {
      "from": "hola",
      "to": "hola!"
    }
  },
  "removed": {
    "bye": {
      "from": "adios",
      "to": null
    }
  }
}
```
