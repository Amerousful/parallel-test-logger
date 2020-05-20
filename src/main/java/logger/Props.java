package logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

public class Props {

    public static final Logger LOGGER = Logger.getLogger(Props.class.getName());

    public final static Properties properties;

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

}
