package listeners;

import io.qameta.allure.Attachment;
import logger.LoggerFactory;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestListener;
import org.testng.ITestResult;

import static logger.Props.properties;

public class ParallelListener implements IInvokedMethodListener, ITestListener {

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult result) {
        boolean isParallel = result.getTestContext().getCurrentXmlTest().getParallel().isParallel();
        if (isParallel) {
            LoggerFactory.init(method.getTestMethod().getMethodName());
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        boolean isParallel = result.getTestContext().getCurrentXmlTest().getParallel().isParallel();

        if (isParallel) {
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

        if (isParallel) {
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
    public void onTestSkipped(ITestResult iTestResult) {

    }

    @Attachment(value = "{0}", type = "text/plain")
    public static String textAttachment(String name, String text) {
        return text;
    }

}
