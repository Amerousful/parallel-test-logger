package listeners;

import io.qameta.allure.Attachment;
import logger.LoggerFactory;
import org.testng.*;
import org.testng.xml.XmlSuite;

import static logger.Props.properties;

public class ParallelListener implements IInvokedMethodListener, ITestListener, IDataProviderListener, IConfigurationListener {

    private boolean dataProviderIsParallel;
    public ThreadLocal<String> testNameForConfiguration = new ThreadLocal<>();

    private boolean parallelIsEnabled(ITestResult result) {
        XmlSuite.ParallelMode parallelInfo = result.getTestContext().getCurrentXmlTest().getParallel();

        return parallelInfo.isParallel() ||
                dataProviderIsParallel ||
                parallelInfo.name().equalsIgnoreCase("tests");
    }

    @Override
    public void beforeConfiguration(ITestResult tr, ITestNGMethod tm) {
        testNameForConfiguration.set(tm.getMethodName());
    }

    @Override
    public void beforeDataProviderExecution(IDataProviderMethod dataProviderMethod, ITestNGMethod method, ITestContext iTestContext) {
        dataProviderIsParallel = dataProviderMethod.isParallel();
    }

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult result) {
        if (parallelIsEnabled(result)) {

            if (testNameForConfiguration.get() != null) {
                LoggerFactory.init(method.getTestMethod().getMethodName(), testNameForConfiguration.get());
            } else {
                LoggerFactory.init(method.getTestMethod().getMethodName());
            }
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        if (parallelIsEnabled(result)) {
            String logs = LoggerFactory.infoLog(result.getName());
            System.out.println(logs);

            String allureProps = properties.getProperty("allure");
            boolean enableAllure = allureProps == null || Boolean.parseBoolean(allureProps);


            if (enableAllure) {
                textAttachment("LOGS " + result.getName(), logs);
            }
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        if (parallelIsEnabled(result)) {
            String logs = LoggerFactory.fullLog(result.getName());
            System.out.println(logs);

            String allureProps = properties.getProperty("allure");
            boolean enableAllure = allureProps == null || Boolean.parseBoolean(allureProps);

            if (enableAllure) {
                textAttachment("LOGS " + result.getName(), logs);
            }
        }

    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult result, ITestContext context) {

        // BeforeSuite runs with threadId 1. For ThreadId 1 initialized console logger.
        if (Thread.currentThread().getId() != 1) {

            if (parallelIsEnabled(result)) {
                if (method.isConfigurationMethod()) {
                    String logs = LoggerFactory.fullConfigLog(result.getName());

                    // Need flush appender if threads count less than tests count.
                    LoggerFactory.flushAppender(result.getName());

                    System.out.println(logs);

                    String allureProps = properties.getProperty("allure");
                    boolean enableAllure = allureProps == null || Boolean.parseBoolean(allureProps);

                    if (enableAllure) {
                        textAttachment("LOGS " + result.getName(), logs);
                    }
                }
            }
        }
    }

    @Override
    public void onTestSkipped(ITestResult iTestResult) {

    }

    @Attachment(value = "{0}", type = "text/plain")
    public static String textAttachment(String name, String text) {
        return text;
    }

}
