package logger;

import org.apache.log4j.*;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static logger.Props.properties;
import static org.apache.log4j.Logger.getLogger;

public class LoggerFactory {

    private static final ConcurrentMap<Long, Logger> byThreadIdLogs = new ConcurrentHashMap<>();
    private static final ConcurrentMap<String, Logger> byClassLogs = new ConcurrentHashMap<>();
    private static final String CONSOLE_LAYOUT = "[%p] %d{HH:mm:ss} [%l]- %m%n";
    private static final String PARALLEL_LAYOUT = "t:%X{threadId} [%p] %d{HH:mm:ss} [%l] - %m%n";

    public static Logger init(String testName) {
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

    public static Logger log(String customPattern) {
        Long threadId = Thread.currentThread().getId();
        Logger logger = byThreadIdLogs.get(threadId);
        if (threadId == 1 || Objects.isNull(logger)) {
            return setConsoleAppender(3, customPattern);
        }
        return byThreadIdLogs.get(threadId);
    }

    public static Logger log() {
        return log("");
    }

    public static Logger log(int nestedLevel) {
        Long threadId = Thread.currentThread().getId();
        Logger logger = byThreadIdLogs.get(threadId);
        if (threadId == 1 || Objects.isNull(logger)) {
            return setConsoleAppender(nestedLevel, "");
        }
        return byThreadIdLogs.get(threadId);
    }

    private static Logger setConsoleAppender(int nestedLevel, String customPattern) {
        String className = Thread.currentThread().getStackTrace()[nestedLevel].getClassName();

        Logger logger = byClassLogs.get(className);
        if (Objects.isNull(logger)) {

            String pattern;

            String patternProps = properties.getProperty("single_pattern");

            pattern = customPattern.isEmpty()
                    ? patternProps == null ? CONSOLE_LAYOUT : patternProps
                    : customPattern;

            final ConsoleAppender consoleAppender = new ConsoleAppender(new PatternLayout(pattern), "System.out");

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

    public static String fullConfigLog(String testName) {
        Logger log = log();
        ParallelAppender appender = (ParallelAppender) log.getAppender(testName);

        return appender.getAllConfigurationLogs();
    }

}
