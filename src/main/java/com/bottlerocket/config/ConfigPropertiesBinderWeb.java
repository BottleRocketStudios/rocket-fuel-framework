package com.bottlerocket.config;

import com.bottlerocket.utils.Logger;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.Properties;

/**
 * Created by ford.arnett on 8/10/18
 */
public class ConfigPropertiesBinderWeb extends ConfigPropertiesBinder {

    public ConfigPropertiesBinderWeb() {
    }

    /**
     * Set the variables which are needed to run the application. These are read in from a properties file or passed in from the command line.
     *
     */
    @Override
    public void loadConfigVariablesFromFile(Properties rawProperties, AutomationConfigProperties configProperties) {
        //load common config before loading Web specific
        super.loadConfigVariablesFromFile(rawProperties, configProperties);


        //Web specific configs go here
        configProperties.browserWindowSize = rawProperties.getProperty("BROWSER_WINDOW_SIZE");
        configProperties.driverPath = rawProperties.getProperty("DRIVER_PATH", "");
        configProperties.headlessChrome = convertToBoolean(rawProperties.getProperty("HEADLESS_CHROME"), false);
        configProperties.disableBrowserExtensions = convertToBoolean(rawProperties.getProperty("DISABLE_BROWSER_EXTENSIONS"), false);
        configProperties.enableRemoteOrigins = convertToBoolean(rawProperties.getProperty("ENABLE_REMOTE_ORIGINS"), false);
        /**
         * Capabilities
         */

    }

    @Override
    public void setCapabilities(AutomationConfigProperties configProperties){
        //Set common capabilities
        super.setCapabilities(configProperties);

        //Web specific capabilities go here

        return;
    }

    @Override
    protected void loadGradleValues(AutomationConfigProperties configProperties) {
        super.loadGradleValues(configProperties);

        String browserName = System.getProperty(gradleKey("browser", configProperties.projectName));
        configProperties.browserName = browserName != null && !browserName.isEmpty() ? browserName : configProperties.browserName;
        Logger.log("Browser name is " + browserName);

        if(configProperties.screenRecord) {
            Logger.log("Screen recording not yet enabled for web");
        }
    }

    public static ChromeOptions getChromeOptions(AutomationConfigProperties configProperties) {

        final ChromeOptions chromeOptions = new ChromeOptions();
        if (configProperties.browserWindowSize != null) {
            chromeOptions.addArguments("--window-size=" + configProperties.browserWindowSize);
        }
        if (configProperties.headlessChrome) {
            chromeOptions.addArguments("--headless");
        }
        if (configProperties.disableBrowserExtensions) {
            chromeOptions.addArguments("--disable-extensions");
        }
        if (configProperties.enableRemoteOrigins) {
            chromeOptions.addArguments("--remote-allow-origins=*");
        }

        return chromeOptions;
    }
}
