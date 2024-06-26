package com.bottlerocket.httphandler;

import java.util.HashMap;

/**
 * Created by ford.arnett on 2/7/17.
 */
public class HttpMessage {
    //These may need to be their own objects and contain everything in it
    String request;
    //These may need to be their own objects and contain everything in it
    String response;
    public HashMap<String,String> requestBody = new HashMap<>();
    public String responseStatus = "";
    public String call = "";

    public String getRequestBodyValue(String key) {
        return requestBody.get(key);
    }

}
