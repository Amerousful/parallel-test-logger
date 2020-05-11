package logger;

import org.apache.log4j.*;

import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.apache.log4j.Logger.getLogger;

public class LoggerFactory {

    private static final ConcurrentMap<Long, Logger> byThreadIdLogs = new ConcurrentHashMap<>();
    private static final ConcurrentMap<String, Logger> byClassLogs = new ConcurrentHashMap<>();
    private static final String CONSOLE_LAYOUT = "[%p] %d{HH:mm:ss} [%l]- %m%n";
    private static final String PARALLEL_LAYOUT = "t:%X{threadId} [%p] %d{HH:mm:ss} [%l] - %m%n";

    public static Logger init(String testName, Properties properties) {
        Long threadId = Thread.currentThread().getId();

        MDC.put("threadId", threadId);

        String patternProps = properties.getProperty("pattern");
        String pattern = patternProps == null ? PARALLEL_LAYOUT : patternProps;

        ParallelAppender appender = new ParallelAppender(new PatternLayout(pattern));
        appender.setName(testName);

        Logger logger = getLogger(String.valueOf(threadId));
        logger.setAdditivity(false);
        logger.addAppender(appender);
        logger.setLevel(Level.DEBUG);

        byThreadIdLogs.put(threadId, logger);

        return logger;
    }

    public static Logger log() {
        Long threadId = Thread.currentThread().getId();
        Logger logger = byThreadIdLogs.get(threadId);
        if (threadId == 1 || Objects.isNull(logger)) {
            return setConsoleAppender(3);
        }
        return byThreadIdLogs.get(threadId);
    }

    public static Logger log(int nestedLevel) {
        Long threadId = Thread.currentThread().getId();
        Logger logger = byThreadIdLogs.get(threadId);
        if (threadId == 1 || Objects.isNull(logger)) {
            return setConsoleAppender(nestedLevel);
        }
        return byThreadIdLogs.get(threadId);
    }

    private static Logger setConsoleAppender(int nestedLevel) {
        String className = Thread.currentThread().getStackTrace()[nestedLevel].getClassName();

        Logger logger = byClassLogs.get(className);
        if (Objects.isNull(logger)) {
            final ConsoleAppender consoleAppender = new ConsoleAppender(new PatternLayout(CONSOLE_LAYOUT), "System.out");

            logger = Logger.getLogger(className);
            logger.setLevel(Level.DEBUG);
            logger.addAppender(consoleAppender);

            byClassLogs.put(className, logger);
        }

        return logger;
    }

    public static String infoLog(String testName) {
        Logger log = log();
        ParallelAppender appender = (ParallelAppender) log.getAppender(testName);

        return appender.getInfoLogs();
    }

    public static String fullLog(String testName) {
        Logger log = log();
        ParallelAppender appender = (ParallelAppender) log.getAppender(testName);

        return appender.getAllLogs();
    }

}
