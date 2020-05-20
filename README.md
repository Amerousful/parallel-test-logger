# Parallel Test Logger
Logger for structured parallel logs. The biggest pain when running parallel tests is spaghetti logs. Let's
 solve it!
 
 Supported for TestNG.
 
 
## How it works?
Before test start - logger initialization by thread id and test name. Then all logs during tests append to buffer. And
 after test end logs gets.
 
###Example: 
Let's defined simple test class with several tests and run in parallel it.
```java
import static logger.LoggerFactory.log;

public class ParallelTestSample {


    @Test
    void firstTest() {
        log().info("first 1");
        log().info("first 2");
        log().info("first 3");
    }

    @Test
    void secondTest() {
        log().info("second 1");
        log().info("second 2");
        log().info("second 3");
    }

    @Test
    void third() {
        log().info("third 1");
        log().info("third 2");
        log().info("third 3");
    }

}

```
Run xml with `parallel="methods" thread-count="3"`
***
Stdout will be chaotic and mixed:
```text
[INFO] 14:18:42 ParallelTestSample.third()- third 1
[INFO] 14:18:42 ParallelTestSample.secondTest()- second 1
[INFO] 14:18:42 ParallelTestSample.firstTest()- first 1
[INFO] 14:18:42 ParallelTestSample.secondTest()- second 2
[INFO] 14:18:42 ParallelTestSample.third()- third 2
[INFO] 14:18:42 ParallelTestSample.secondTest()- second 3
[INFO] 14:18:42 ParallelTestSample.firstTest()- first 2
[INFO] 14:18:42 ParallelTestSample.third()- third 3
[INFO] 14:18:42 ParallelTestSample.firstTest()- first 3
```

Ok, now we defined parallel listener and run again
```java
import listeners.ParallelListener;

@Listeners({ParallelListener.class})
public class ParallelTestSample {

}
```
###Result:
```text
--- [START] Test: third | Thread id: 14] ---
t:14 [INFO] 15:47:33 ParallelTestSample.third()- third 1
t:14 [INFO] 15:47:33 ParallelTestSample.third()- third 2
t:14 [INFO] 15:47:33 ParallelTestSample.third()- third 3
--- [END] Test: third | Thread id: 14] ---

 --- [START] Test: firstTest | Thread id: 12] ---
t:12 [INFO] 15:47:33 ParallelTestSample.firstTest()- first 1
t:12 [INFO] 15:47:33 ParallelTestSample.firstTest()- first 2
t:12 [INFO] 15:47:33 ParallelTestSample.firstTest()- first 3
--- [END] Test: firstTest | Thread id: 12] ---

 --- [START] Test: secondTest | Thread id: 13] ---
t:13 [INFO] 15:47:33 ParallelTestSample.secondTest()- second 1
t:13 [INFO] 15:47:33 ParallelTestSample.secondTest()- second 2
t:13 [INFO] 15:47:33 ParallelTestSample.secondTest()- second 3
--- [END] Test: secondTest | Thread id: 13] ---
```


Don't initialization logger for class and use in static methods!!!
Call `log()` in static method and tests.

