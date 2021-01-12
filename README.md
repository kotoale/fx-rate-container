# db-test-task
## Task description
Coding Task.

a) You need to write a class / classes that is responsible for keeping Fx rates from Exchange. Assume that you have a feed from an exchange where you get Fx rate and time. For instance, the message looks like: {“ccyPair” : “EURRUB”, “fxRate” : 90.1208, “timestamp” : 1579598352}, where timestamp is an epoch timestamp. Let’s assume that the feed returns a message in the correct order.
You need to implement interface FxRateContainer:

```
FxRateContainer {
/**
Adds Fx Rate for specified currency pair and timestamp to the container
*/
void add(String ccyPair, double fxRate, long timestamp);
/**
Returns Fx Rate for currency pair at the concrete moment of time
*/
double get(String ccyPair, long timestamp);
}
```
The main task is to implement the interface so it has the least possible complexity of adding and getting.

b) You need to add method
```
/**
Returns average Fx Rate in period from start to end
*/
double average(String ccyPair, long start, long end);
```

# Prerequisites
* OpenJDK 11 - https://adoptopenjdk.net/
* Maven 3 - https://maven.apache.org/download.cgi
