package com.bottlerocket.remote;

import com.bottlerocket.webdriverwrapper.WebDriverWrapper;

/**
 *
 * I played around with the idea of adding the test methods to a new implementation of {@link com.bottlerocket.reporters.AutomationReporter}
 * but it seems like overkill for the test options that are here.
 * The drawback of this approach is that it is now to easy to switch off these methods in the event we aren't using Sauce. To help with this,
 * I've added an easy switch to turn of the use of Sauce commands
 *
 *
 * <a href="https://docs.saucelabs.com/basics/test-config-annotation/test-annotation/">Sauce test annotations</a>
 *
 * Created by ford.arnett on 9/30/22
 */
public class SauceExecutor {
    public boolean useSauce = false;

    public SauceExecutor(boolean useSauce) {
        this.useSauce = useSauce;
    }


    public void testPassed(WebDriverWrapper driverWrapper) {
        executeSauceCommand(driverWrapper, "sauce:job-result=passed");
    }

    public void testFailed(WebDriverWrapper driverWrapper) {
        executeSauceCommand(driverWrapper, "sauce:job-result=failed");
    }

    public void setJobName(WebDriverWrapper driverWrapper, String jobName) {
        executeSauceCommand(driverWrapper, "sauce:job-name=" + jobName);
    }

    public void injectImage(WebDriverWrapper driverWrapper, String base64EncodedImage) {
        executeSauceCommand(driverWrapper, "sauce:inject-image=[" + base64EncodedImage + "]");
    }

    public void addTestContext(WebDriverWrapper driverWrapper, String text) {
        executeSauceCommand(driverWrapper, "sauce:context=" + text);
    }

    private void executeSauceCommand(WebDriverWrapper driverWrapper, String command) {
        if(!useSauce) {return;}

        driverWrapper.executeScript(command);

    }
}
