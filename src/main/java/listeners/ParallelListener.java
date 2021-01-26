package listeners;

import io.qameta.allure.Attachment;
import logger.LoggerFactory;
import org.testng.*;

import static logger.Props.properties;

public class ParallelListener implements IInvokedMethodListener, ITestListener, IDataProviderListener {

    public boolean dataProviderIsParallel;

    @Override
    public void beforeDataProviderExecution(IDataProviderMethod dataProviderMethod, ITestNGMethod method, ITestContext iTestContext) {
        dataProviderIsParallel = dataProviderMethod.isParallel();
    }

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult result) {
        boolean isParallel = result.getTestContext().getCurrentXmlTest().getParallel().isParallel();
        if (isParallel || dataProviderIsParallel) {
            LoggerFactory.init(method.getTestMethod().getMethodName());
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        boolean isParallel = result.getTestContext().getCurrentXmlTest().getParallel().isParallel();

        if (isParallel || dataProviderIsParallel) {
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
        boolean isParallel = result.getTestContext().getCurrentXmlTest().getParallel().isParallel();

        if (isParallel || dataProviderIsParallel) {
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

            boolean isParallel = result.getTestContext().getCurrentXmlTest().getParallel().isParallel();

            if (isParallel || dataProviderIsParallel) {
                if (method.isConfigurationMethod()) {
                    String logs = LoggerFactory.fullConfigLog(result.getName());
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
