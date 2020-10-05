package logger;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

import java.util.concurrent.CopyOnWriteArrayList;

import static org.apache.log4j.Level.INFO;

public class ParallelAppender extends AppenderSkeleton {

    final CopyOnWriteArrayList<String> logInfo = new CopyOnWriteArrayList<>();
    final CopyOnWriteArrayList<String> fullLog = new CopyOnWriteArrayList<>();

    public ParallelAppender(Layout layout) {
        this.setLayout(layout);
    }

    @Override
    protected void append(LoggingEvent event) {
        String formatted = getLayout().format(event);
        fullLog.add(formatted);

        if (event.getLevel().equals(INFO)) {
            logInfo.add(formatted);
        }
    }

    @Override
    public void close() {

    }

    @Override
    public boolean requiresLayout() {
        return true;
    }

    public String getAllLogs() {
        return returnTestLogs(fullLog);
    }

    public String getInfoLogs() {
        return returnTestLogs(logInfo);
    }

    public String getAllConfigurationLogs() {
        return returnConfigurationLogs(fullLog);
    }

    private String returnTestLogs(CopyOnWriteArrayList<String> list) {
        return returnLogs(list, MethodType.Test);
    }

    private String returnConfigurationLogs(CopyOnWriteArrayList<String> list) {
        return returnLogs(list, MethodType.Configuration);
    }

    private String returnLogs(CopyOnWriteArrayList<String> list, MethodType methodType) {
        StringBuilder dataOut =
                new StringBuilder("--- [START] " + methodType + " : " + super.getName() + " | Thread id: " + Thread.currentThread().getId() + "] ---\n");
        for (String message : list) {
            dataOut.append(message);
        }
        dataOut.append("--- [END] " + methodType + ": ").append(super.getName()).append(" | Thread id: ").append(Thread.currentThread().getId()).append("] ---\n");

        return dataOut.toString();
    }

}
