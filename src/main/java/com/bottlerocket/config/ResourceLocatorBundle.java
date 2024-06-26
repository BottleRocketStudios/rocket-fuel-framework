package com.bottlerocket.config;

import com.bottlerocket.utils.*;
import org.openqa.selenium.*;

/**
 * Created by ford.arnett on 12/8/21
 */
public abstract class ResourceLocatorBundle {
    public static String runtimePlatform;

    By runtimeBy;
    String runtimeLocator;

    public ResourceLocatorBundle(String runtimeLocator) {
        this.runtimeLocator = runtimeLocator;
    }

    public ResourceLocatorBundle(By runtimeBy) {
        this.runtimeBy = runtimeBy;
    }

    public static ResourceLocatorBundle build(By web, By iOS, By android) {
        //TODO Update this with isSet method in newest framework version
        if((runtimePlatform == null || runtimePlatform.isEmpty())) {
            Logger.log("Runtime platform was not set, unable to determine which platform to use for ResourceLocatorBundle. Check that platform is being set correctly");
            return null;
        }

        if(runtimePlatform.equalsIgnoreCase("web")) {
            return new ResourceLocatorBundleWeb(web);
        } else if (runtimePlatform.equalsIgnoreCase("iOS")) {
            return new ResourceLocatorBundleiOS(iOS);
        } else if (runtimePlatform.equalsIgnoreCase("android")) {
            return new ResourceLocatorBundleAndroid(android);
        } else {
            Logger.log("There was no match for the platform given. Check if there is a spelling issue or a new platform is being used but not accounted for. ");
            return null;
        }
    }

    public static ResourceLocatorBundle build(By locator) {
        //TODO Update this with isSet method in newest framework version
        if((runtimePlatform == null || runtimePlatform.isEmpty())) {
            Logger.log("Runtime platform was not set, unable to determine which platform to use for ResourceLocatorBundle. Check that platform is being set correctly");
            return null;
        }

        switch(runtimePlatform.toLowerCase()) {
            case "web" -> {return  new ResourceLocatorBundleWeb(locator);}
            case "ios" -> {return new ResourceLocatorBundleiOS(locator);}
            case "android" -> {return new ResourceLocatorBundleAndroid(locator);}
            default         ->   {
                Logger.log("There was no match for the platform given. Check if there is a spelling issue or a new platform is being used but not accounted for. ");
                return null;
            }
        }
    }

    public static ResourceLocatorBundle build(String web, String iOS, String android) {
        if(runtimePlatform == null || runtimePlatform.isEmpty()) {
            Logger.log("Runtime platform was not set, unable to determine which platform to use for ResourceLocatorBundle. Check that platform is being set correctly");
            return null;
        }

        if(runtimePlatform.equalsIgnoreCase("web")) {
            return new ResourceLocatorBundleWeb(web);
        } else if (runtimePlatform.equalsIgnoreCase("iOS")) {
            return new ResourceLocatorBundleiOS(iOS);
        } else if (runtimePlatform.equalsIgnoreCase("android")) {
            return new ResourceLocatorBundleAndroid(android);
        } else {
            Logger.log("There was no match for the platform given. Check if there is a spelling issue or a new platform is being used but not accounted for. ");
            return null;
        }

    }

    public static ResourceLocatorBundle build(By iOS, By android) {
        return build(null, iOS, android);
    }

    public static ResourceLocatorBundle build(String iOS, String android) {
        return build(null, iOS, android);
    }

    public String getLocator() {
        return runtimeLocator;
    }

    public By getBy() {
        return runtimeBy;
    }


    //Perhaps some image processing stuff here?

}
