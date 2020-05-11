package listeners;


import io.qameta.allure.Attachment;
import logger.LoggerFactory;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;


public class ParallelListener implements IInvokedMethodListener, ITestListener {

    public static final Logger LOGGER = Logger.getLogger(ParallelListener.class.getName());

    private final static Properties properties;

    static {
        properties = loadProperties();
    }

    public static Properties loadProperties() {
        String propertiesFileName = "parallel_log.properties";

        Properties mainProperties = new Properties();
        try (InputStream input = LoggerFactory.class.getClassLoader().getResourceAsStream(propertiesFileName)) {

            if (input != null) {
                mainProperties.load(input);
            } else {
                LOGGER.info("Sorry, unable to find " + propertiesFileName);
            }

        } catch (IOException e) {  
            e.printStackTrace();
        }
        return mainProperties;
    }

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult result) {
        boolean isParallel = result.getTestContext().getCurrentXmlTest().getParallel().isParallel();
        if (isParallel) {
            LoggerFactory.init(method.getTestMethod().getMethodName(), properties);
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
