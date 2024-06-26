package com.bottlerocket.utils;

import com.bottlerocket.errorhandling.WebDriverWrapperException;
import com.bottlerocket.webdriverwrapper.AppiumDriverWrapper;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

/**
 * Created by ford.arnett on 8/31/15.
 */
public abstract class InputUtils {
    AppiumDriverWrapper driver;

    public InputUtils(AppiumDriverWrapper driverWrapper){
        driver = driverWrapper;
    }

    public void sendKeysNumberPicker(WebElement numberPicker, By pickerElementsLocator, By numberPickerCloseButton, List<String> input) throws WebDriverWrapperException {
        //Open up the date picker
        numberPicker.click();

        List<WebElement> pickerElements = driver.elements(pickerElementsLocator);

        //Protect against elements not being found
        if(pickerElements.size() == 0){
            //The best we can do here is try to close the popup with the values being unchanged
            driver.element(numberPickerCloseButton).click();
            return;
        }

/*        //Wait for the first element in the picker to be ready
        driver.driverWait.until(ExpectedConditions.elementToBeClickable(pickerElements.get(0)));
        for(int i = 0; i< input.size(); i++){
            //Get the picker element (column) and put our input into it
            pickerElements.get(i).sendKeys(input.get(i));
        }*/

        //Wait for element in picker to be ready
        driver.driverWait.until(ExpectedConditions.elementToBeClickable(pickerElements.get(1)));
        //For some reason the first element doesn't work when it goes first. Must be some timing issue
        pickerElements.get(1).sendKeys(input.get(1));
        pickerElements.get(0).sendKeys(input.get(0));

        driver.element(numberPickerCloseButton).click();
    }

    /**
     * Does not try to correct itself in the event of the send keys/paste bug. This should be used for any case where the send keys paste bug has not been observed.
     *
     * Suppresses exception thrown by hide keyboard. Error seems to get thrown too frequently, and shouldn't cause a test to fail
     *
     * @param webElement
     * @param keys
     */
    public abstract void sendKeysHideKeyboard(WebElement webElement, String keys);

    public void sendKeysHideKeyboard(WebElement webElement, long keys){
        sendKeysHideKeyboard(webElement, String.valueOf(keys));
    }

    /**
     * This is currently very limited in use. It does not seem to work on webviews, and does not seem to work on password fields. The only confirmed use of this method is in the sign in form
     *
     * There is an error when using {@link WebElement#sendKeys(CharSequence...)} where around 1 out of every 10 times it will use a long press to select the
     * input box which will prompt the paste operation, then the send keys will paste and then send the keys. This is a confirmed appium bug, which I have not been
     * able to find a good solution to. This is an ugly solution, however, it should fix the problem when it does happen.
     *
     * @param webElement webElement to send the keys to
     * @param keys the keys to send
     */
    public void sendKeysSafe(WebElement webElement, CharSequence keys){
        webElement.click();
        //webElement.clear();
        webElement.sendKeys(keys);


       /* webElement.sendKeys(keys);
        //This should only run once since the odds of this issue (~10%) happening twice are very small, however, in an effort to make this more secure
        //I am giving this a few chances to try and fix itself. If this happens more than 10 times, there is something else wrong which we will not be able to fix here.
        //This could be done in a while loop, however, given the buggy nature of appium's sendKeys I'd like to be safe and avoid a possible dead lock
        for(int i=0; i< 10; i++){
            if(checkTextEquals(webElement, keys)) {
                break;
            }
            else {
                clearTextField(webElement);
                webElement.sendKeys(keys);
            }
        }*/

    }

    /**
     * Enter a number into a web element one digit at a time, waiting between digits. This is to protect against digits getting lost by entering them into an input too fast. This can happen when you have something like a
     * credit card field which is separated into parts, sometimes the pauses cause numbers to be lost.
     *
     * @param webElement element which will receive the keys
     * @param fullNumber number to send
     */
    public void sendNumbersSafe(WebElement webElement, String fullNumber){
        driver.driverWait.until(ExpectedConditions.elementToBeClickable(webElement));
        webElement.click();

        for(int i=0;i < fullNumber.length(); i++) {
            int digit = Character.getNumericValue(fullNumber.charAt(i));

            driver.driverWait.until(ExpectedConditions.elementToBeClickable(webElement));
            String keyInt = Integer.toString(AndroidKey.DIGIT_0.getCode() + digit);
            driver.pressKeyCode(AndroidKey.valueOf(keyInt));
        }
    }

    private void clearTextField(WebElement webElement){

        int stringLength = webElement.getAttribute("name").length();

        for (int i = 0; i < stringLength; i++) {
            driver.pressKeyCode(AndroidKey.DPAD_RIGHT);
        }

        for (int i = 0; i < stringLength; i++) {
            driver.pressKeyCode(AndroidKey.DEL);
        }
    }

    private boolean checkTextEquals(WebElement webElement, CharSequence keys){
        return webElement.getAttribute("name").equals(keys);
    }

    public abstract void setTextField(String value);

    public abstract void submitForm();
}
