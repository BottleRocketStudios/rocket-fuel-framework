package com.bottlerocket.config;

import com.bottlerocket.utils.Logger;
import io.appium.java_client.remote.IOSMobileCapabilityType;

import java.util.Properties;
import java.util.Random;

/**
 * Created by ford.arnett on 11/3/15.
 */
public class ConfigPropertiesBinderIos extends ConfigPropertiesBinder {

    public ConfigPropertiesBinderIos() {
    }

    /**
     * Set the variables which are needed to run the application. These are read in from a properties file.
     *
     */
    @Override
    public void loadConfigVariablesFromFile(Properties rawProperties, AutomationConfigProperties configProperties) {
        //This is ok to get overriden by gradle values in the super.
        configProperties.wdaPort = rawProperties.getProperty("WDA_PORT");

        //load common config before loading ios specific
        super.loadConfigVariablesFromFile(rawProperties, configProperties);

        //Ios specific configs go here
        /**
         * Capabilities
         */
        configProperties.appPath = rawProperties.getProperty("IPA_LOCATION");
        configProperties.xcuiTestDriver = convertToBoolean(rawProperties.getProperty("XCUI_TEST_DRIVER"), false);
    }

    @Override
    public void setCapabilities(AutomationConfigProperties configProperties){
        //Set common capabilities
        super.setCapabilities(configProperties);

        //Ios specific capabilities go here
        setNonNullCap(configProperties.capabilities, IOSMobileCapabilityType.XCODE_ORG_ID, configProperties.xcodeOrgId);
        setNonNullCap(configProperties.capabilities, IOSMobileCapabilityType.XCODE_SIGNING_ID, configProperties.xcodeSigningId);
        setNonNullCap(configProperties.capabilities, "bundleId", configProperties.bundleId);
        setNonNullCap(configProperties.capabilities, "realDeviceLogger", "/usr/local/lib/node_modules/deviceconsole/deviceconsole");

        Logger.log("WDA port set to " + configProperties.wdaPort);
        setNonNullCap(configProperties.capabilities, "wdaLocalPort", configProperties.wdaPort);
    }

    @Override
    protected void loadGradleValues(AutomationConfigProperties configProperties) {
        super.loadGradleValues(configProperties);

        String port = System.getProperty(gradleKey("port", configProperties.projectName));
        configProperties.localDriverURL = port != null && !port.isEmpty() ? "http://127.0.0.1:" + port + "/wd/hub" : configProperties.localDriverURL;
        Logger.log("Port is " + configProperties.localDriverURL);

        String deviceId = System.getProperty(gradleKey("deviceid", configProperties.projectName));
        configProperties.udid = deviceId != null && !deviceId.isEmpty() ? deviceId : configProperties.udid;
        Logger.log("Udid is " + configProperties.udid);

        String wdaport = System.getProperty(gradleKey("wdaport", configProperties.projectName));
        Random random = new Random();
        configProperties.wdaPort = wdaport != null && !wdaport.isEmpty() ?  wdaport : String.valueOf(8000 + random.nextInt(100));
        Logger.log("Wda Port is " + configProperties.wdaPort);

        if(configProperties.screenRecord) {
            String uniquefolder = System.getProperty(gradleKey("uniquefolder", configProperties.projectName));
            configProperties.screenRecordDirectory = uniquefolder != null && !uniquefolder.isEmpty() ? uniquefolder + "/videos/" : configProperties.screenRecordDirectory;
            Logger.log("Screen recording enabled. Videos will be located at " + configProperties.screenRecordDirectory);
        }
    }
}
