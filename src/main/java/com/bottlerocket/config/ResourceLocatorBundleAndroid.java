package com.bottlerocket.config;

import org.openqa.selenium.*;

/**
 * Created by ford.arnett on 4/14/22
 */
public class ResourceLocatorBundleAndroid extends ResourceLocatorBundle {
    public ResourceLocatorBundleAndroid(By android) {
        super(android);
    }

    public ResourceLocatorBundleAndroid(String android) {
        super(android);
    }
}
