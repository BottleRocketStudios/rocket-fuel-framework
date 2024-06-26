package com.bottlerocket.config;

import com.bottlerocket.utils.Logger;
import io.appium.java_client.remote.AndroidMobileCapabilityType;

import java.util.Properties;

/**
 * Created by ford.arnett on 11/3/15.
 */
public class ConfigPropertiesBinderAndroid extends ConfigPropertiesBinder {

    public ConfigPropertiesBinderAndroid() {

    }

    /**
     * Set the variables which are needed to run the application. These are read in from a properties file or passed in from the command line.
     *
     */
    @Override
    public void loadConfigVariablesFromFile(Properties rawProperties, AutomationConfigProperties configProperties) {
        //load common config before loading Android specific
        super.loadConfigVariablesFromFile(rawProperties, configProperties);


        //Android specific configs go here
        /**
         * Capabilities
         */
        configProperties.appPath = rawProperties.getProperty("APK_LOCATION");
        configProperties.headlessAndroid = convertToBoolean(rawProperties.getProperty("HEADLESS_EMULATOR"), false);
    }

    @Override
    public void setCapabilities(AutomationConfigProperties configProperties){
        //Set common capabilities
        super.setCapabilities(configProperties);

        //Android specific capabilities go here
        setNonNullCap(configProperties.capabilities, AndroidMobileCapabilityType.APP_ACTIVITY, configProperties.appActivity);
        setNonNullCap(configProperties.capabilities, AndroidMobileCapabilityType.APP_PACKAGE, configProperties.appPackage);
        setNonNullCap(configProperties.capabilities, AndroidMobileCapabilityType.APP_WAIT_ACTIVITY, configProperties.appWaitActivity);
        setNonNullCap(configProperties.capabilities, AndroidMobileCapabilityType.APP_WAIT_PACKAGE, configProperties.appWaitPackage);

        if(configProperties.headlessAndroid){
            configProperties.capabilities.setCapability("isHeadless", true);
        }
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

        if(configProperties.screenRecord) {
            String uniquefolder = System.getProperty(gradleKey("uniquefolder", configProperties.projectName));
            configProperties.screenRecordDirectory = uniquefolder != null && !uniquefolder.isEmpty() ? uniquefolder + "/videos/" : configProperties.screenRecordDirectory;
            Logger.log("Screen recording enabled. Videos will be located at " + configProperties.screenRecordDirectory);
        }
    }
}
