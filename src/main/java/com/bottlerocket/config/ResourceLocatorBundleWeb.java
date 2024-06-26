package com.bottlerocket.config;

import org.openqa.selenium.*;

/**
 * Created by ford.arnett on 4/11/22
 */
public class ResourceLocatorBundleWeb extends ResourceLocatorBundle {

    public ResourceLocatorBundleWeb(By web) {
        super(web);
    }

    public ResourceLocatorBundleWeb(String web) {
        super(web);
    }

}
