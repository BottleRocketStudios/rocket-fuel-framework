package com.bottlerocket.utils;

import com.bottlerocket.webdriverwrapper.AppiumDriverWrapper;
import com.bottlerocket.webdriverwrapper.AppiumDriverWrapperIos;
import org.openqa.selenium.WebElement;

/**
 * Created by ford.arnett on 1/27/16.
 */
public class InputUtilsIos extends InputUtils{
    public InputUtilsIos(AppiumDriverWrapper driverWrapper) {
        super(driverWrapper);
    }

    @Override
    public void sendKeysHideKeyboard(WebElement webElement, String keys) {
        webElement.click();
        webElement.sendKeys(keys);
        //not happy about this being here, but we don't currently have a hook into sendKeys
        driver.getReporter().addInfoToReport("Sending " + keys + " to keyboard");

        ((AppiumDriverWrapperIos)driver).hideKeyboard("Hide keyboard");
    }


    @Override
    public void setTextField(String value){
        //Should this be pulled out of driver wrapper and put here?
        ((AppiumDriverWrapperIos)driver).setTextField(value, 0);
        //not happy about this being here, but we don't currently have a hook into setTextField
        driver.getReporter().addInfoToReport("Setting text field to " + value);
    }

    @Override
    public void submitForm() {
        ((AppiumDriverWrapperIos)driver).hideKeyboard("Go");
    }
}
