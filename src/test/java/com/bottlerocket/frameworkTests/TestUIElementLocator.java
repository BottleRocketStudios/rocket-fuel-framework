package com.bottlerocket.frameworkTests;

import com.bottlerocket.utils.Logger;
import com.bottlerocket.webdriverwrapper.uiElementLocator.TestPlatform;
import com.bottlerocket.webdriverwrapper.uiElementLocator.UIElementLocator;
import com.bottlerocket.webdriverwrapper.uiElementLocator.UIElementSelector;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.support.locators.RelativeLocator;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static com.bottlerocket.webdriverwrapper.uiElementLocator.LocatorStrategy.ID;
import static com.bottlerocket.webdriverwrapper.uiElementLocator.LocatorStrategy.XPATH;
import static com.bottlerocket.webdriverwrapper.uiElementLocator.TestPlatform.ANDROID;
import static com.bottlerocket.webdriverwrapper.uiElementLocator.TestPlatform.IOS;

public class TestUIElementLocator {

    @Test
    public void testUIElementLocator() {
        UIElementLocator uiElementLocator = new UIElementLocator();

        UIElementSelector uiElementSelector1 = new UIElementSelector(ANDROID, ID, "selector1", 1);
        UIElementSelector uiElementSelector2 = new UIElementSelector(ANDROID, XPATH, "selector2", 2);
        UIElementSelector uiElementSelector3 = new UIElementSelector(ANDROID, XPATH, "selector3", 3);
        UIElementSelector uiElementSelector4 = new UIElementSelector(IOS, ID, "selector4");

        uiElementSelector1.setTag("tag");
        uiElementSelector2.setTag("tag");
        uiElementSelector3.setTag("tag");
        uiElementSelector4.setTag("tag");

        uiElementSelector1.setTag("tag1");
        uiElementSelector2.setTag("tag2");
        uiElementSelector3.setTag("tag3");
        uiElementSelector4.setTag("tag4");

        uiElementLocator.addElementSelector(uiElementSelector1);
        uiElementLocator.addElementSelector(uiElementSelector2);
        uiElementLocator.addElementSelector(uiElementSelector3);
        uiElementLocator.addElementSelector(uiElementSelector4);

        ArrayList<String> tags = new ArrayList<>();
        ArrayList<By> locators = new ArrayList<>();
        String testResult = "";

        try {
            String selector1 = uiElementLocator.getPlatformElementSelector(ANDROID).getBestElementSelector().getSelector();
            Assert.assertEquals(selector1, "selector1");
            testResult = "pass";
        } catch (AssertionError e) {
            testResult = "fail";
        } finally {
            System.out.println("Test UIElementSelector.getSelector(): " + testResult);
            tags.clear();
            testResult = "";
        }

        try {
            tags.add("tag1");
            locators = new ArrayList(uiElementLocator.getAllLocators(tags));
            Assert.assertEquals(1, locators.size());
            testResult = "pass";
        } catch (AssertionError e) {
            testResult = "fail";
        } finally {
            System.out.println("Test 1: " + testResult);
            tags.clear();
            testResult = "";
        }

        try {
            tags.add("tag");
            locators = new ArrayList(uiElementLocator.getAllLocators(ANDROID, XPATH, tags));
            Assert.assertEquals(2, locators.size());
            testResult = "pass";
        } catch (AssertionError e) {
            testResult = "fail";
        } finally {
            System.out.println("Test 2: " + testResult);
            tags.clear();
            testResult = "";
        }

        try {
            locators = new ArrayList(uiElementLocator.getAllLocators(ANDROID));
            Assert.assertEquals(3, locators.size());
            testResult = "pass";
        } catch (AssertionError e) {
            testResult = "fail";
        } finally {
            System.out.println("Test 3: " + testResult);
            tags.clear();
            testResult = "";
        }

        try {
            locators = new ArrayList(uiElementLocator.getAllLocators(IOS));
            Assert.assertEquals(1, locators.size());
            testResult = "pass";
        } catch (AssertionError e) {
            testResult = "fail";
        } finally {
            System.out.println("Test 4: " + testResult);
            tags.clear();
            testResult = "";
        }

        try {
            tags.add("tag");
            locators = new ArrayList(uiElementLocator.getAllLocators(tags));
            Assert.assertEquals(4, locators.size());
            testResult = "pass";
        } catch (AssertionError e) {
            testResult = "fail";
        } finally {
            System.out.println("Test 5: " + testResult);
            tags.clear();
            testResult = "";
        }

    }

    @Test (expectedExceptions = {InvalidArgumentException.class})
    public void testUIElementSelectorBuilderShouldThrowException() {
        UIElementSelector badElement = new UIElementSelector.UIElementSelectorBuilder(IOS, ID).build();
    }

    @Test
    public void testUIElementSelectorBuilder() {
        UIElementSelector element1 = new UIElementSelector.UIElementSelectorBuilder()
                .setTestPlatform(ANDROID)
                .setLocatorStrategy(XPATH)
                .setSelector("//*[@text='an xpath']")
                .setRank(2)
                .setTags(new ArrayList<String>(Arrays.asList("tag1", "tag2")))
                .addTag("tag3")
                .addTags(new String[]{"tag4", "tag5"})
                .setRelativeLocator(RelativeLocator.with(By.id("id")).below(By.id("title")))
                .setCustomBy(By.id("customBy"))
                .build();

        String testResult = "";

        try {
            Assert.assertEquals(ANDROID, element1.getTestPlatform());
            testResult = "pass";
        } catch (AssertionError e) {
            testResult = "fail";
        } finally {
            Logger.log("Test platform test: " + testResult);
            testResult = "";
        }

        try {
            Assert.assertEquals(XPATH, element1.getLocatorStrategy());
            testResult = "pass";
        } catch (AssertionError e) {
            testResult = "fail";
        } finally {
            Logger.log("Locator strategy test: " + testResult);
            testResult = "";
        }

        try {
            Assert.assertEquals("//*[@text='an xpath']", element1.getSelector());
            testResult = "pass";
        } catch (AssertionError e) {
            testResult = "fail";
        } finally {
            Logger.log("Selector test: " + testResult);
            testResult = "";
        }

        try {
            Assert.assertEquals(2, element1.getRank());
            testResult = "pass";
        } catch (AssertionError e) {
            testResult = "fail";
        } finally {
            Logger.log("Rank test: " + testResult);
            testResult = "";
        }

        try {
            Assert.assertEquals(5, element1.getTags().size());
            testResult = "pass";
        } catch (AssertionError e) {
            testResult = "fail";
        } finally {
            Logger.log("Tags test: " + testResult);
            testResult = "";
        }

        try {
            Assert.assertTrue(element1.getRelativeLocatorResult() instanceof By);
            testResult = "pass";
        } catch (AssertionError e) {
            testResult = "fail";
        } finally {
            Logger.log("CustomBy test: " + testResult);
            testResult = "";
        }

        try {
            Assert.assertTrue(element1.getCustomBy() instanceof By);
            testResult = "pass";
        } catch (AssertionError e) {
            testResult = "fail";
        } finally {
            Logger.log("CustomBy test: " + testResult);
            testResult = "";
        }

    }

    @Test
    public void testUIElementLocatorBuilder() {
        String testResult = "";

        UIElementLocator uiElementLocator = new UIElementLocator.UIElementLocatorBuilder()
                .addSelector(ANDROID, ID, "anID")
                .addSelector(IOS, XPATH, "//*", "iOSTag")
                .addSelector(new UIElementSelector.UIElementSelectorBuilder()
                        .setTestPlatform(ANDROID)
                        .setLocatorStrategy(XPATH)
                        .setSelector("//*")
                        .addTag("lowRank")
                        .setRank(99)
                        .build()
                )
        .build();


        ArrayList<UIElementSelector> androidSelectors = new ArrayList<>(uiElementLocator.getAllSelectors(ANDROID));
        ArrayList<UIElementSelector> iosSelectors = new ArrayList<>(uiElementLocator.getAllSelectors(IOS));


        Assert.assertEquals(2, androidSelectors.size());
        Assert.assertEquals(TestPlatform.ANDROID, androidSelectors.get(0).getTestPlatform());
        Assert.assertEquals(1, iosSelectors.size());
        Assert.assertEquals(TestPlatform.IOS, iosSelectors.get(0).getTestPlatform());

        UIElementSelector iosTagSelector = new UIElementSelector();
        for (UIElementSelector selector : iosSelectors) {
            if (selector.getTags().contains("iOSTag")) {
                iosTagSelector = selector;
                break;
            }
        }

        Assert.assertEquals(1, uiElementLocator.getAllLocators(Arrays.asList("iOSTag")).size());
        Assert.assertEquals(TestPlatform.IOS, iosTagSelector.getTestPlatform());
    }
}
