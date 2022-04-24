# bumbleB
BDD framework for my master's thesis project



### Known issues
* ExampleProcessor can't handle step prefixes/keywords with more than one word
* ExampleProcessor can't handle cases where custom prefix/keyword methods are called without a static import, e.g. ExtendedFramework.after() instead of just after()