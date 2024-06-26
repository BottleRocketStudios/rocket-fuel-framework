package com.bottlerocket.utils;

import com.bottlerocket.webdriverwrapper.AppiumDriverWrapper;
import io.appium.java_client.android.nativekey.AndroidKey;
import org.openqa.selenium.WebElement;

/**
 * Created by ford.arnett on 1/27/16.
 */
public class InputUtilsAndroid extends InputUtils{
    public InputUtilsAndroid(AppiumDriverWrapper driverWrapper) {
        super(driverWrapper);
    }

    @Override
    public void sendKeysHideKeyboard(WebElement webElement, String keys){
        webElement.sendKeys(keys);
        try {
            driver.hideKeyboard();
        }
        catch(Exception ex){
            ErrorHandler.printErr("Device keyboard not present",ex);
        }
    }

    @Override
    public void submitForm() {
        driver.pressKeyCode(AndroidKey.ENTER);
    }

    @Override
    public void setTextField(String value){
        //empty for now
    }

}
