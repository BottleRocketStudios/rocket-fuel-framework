package com.bottlerocket.httphandler;

import com.bottlerocket.httphandler.mitmproxy.MitmProxyRunner;
import com.bottlerocket.utils.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ford.arnett on 1/31/17.
 */
public class HttpMessageParser {

    public static String decode(String s) {
        if(s == null || s.isEmpty()) {
            return s;
        }
        try {
            return URLDecoder.decode(s, "UTF-8");
        } catch (Exception e) {
            Logger.log("Unable to decode " + s + " returning undecoded string");
            return s;
        }
    }

    public List<HttpMessage> getMessageByTextInURL(List<HttpMessage> httpMessages, String urlPart) {
        List<HttpMessage> results = new ArrayList<>();
        for (HttpMessage message : httpMessages) {
            if(message.call.contains(urlPart)) {
                results.add(message);
            }
        }

        return results;
    }

    /**
     * Parses the output to retrieve the HttpMessages. Any extra information is removed.
     *
     * @param mitmOutput
     * @return
     */
    public List<HttpMessage> mitmOutToHttpMessages(List<String> mitmOutput, boolean decode) {
        List<HttpMessage> results = new ArrayList<>();
        //Remove mitm loading script message
        mitmOutput.remove(0);
        //Remove mitm script exit message
        mitmOutput.remove(mitmOutput.size() - 1);

        //Remove any additional info added
        for(int i = 0; i < mitmOutput.size(); i++) {
            String s = mitmOutput.get(i);
            if(s.startsWith("Info:")) {
                mitmOutput.remove(i);
            }
        }


        boolean newMessage = false;
        HttpMessage httpMessage = new HttpMessage();
        for (int i = 0; i < mitmOutput.size(); i++) {
            if(newMessage) {
                httpMessage = new HttpMessage();
                newMessage = false;
            }

            String currentLine = mitmOutput.get(i);
            if (decode) {
                currentLine = decode(currentLine);
            }

            //Parse each line and store in the appropriate place depending on what the line is. Lines are currently defined as response body, http call, or http status response code.
            //Response code is defined by a line looking like << and three numbers with the first digit 1-5. Http call is assumed the line before, and the rest is assumed as the response body.
            //If more info is added later this will need to be updated. Note the request body identifier only is on the first line of the body and the body may span multiple lines.
            if(httpSequenceEnd(currentLine)) {
                httpMessage.responseStatus = currentLine;
                results.add(httpMessage);
                newMessage = true;
            } else if (i + 1 < mitmOutput.size() && httpSequenceEnd(mitmOutput.get(i + 1))) {
                httpMessage.call = currentLine;
            } else {
                //Remove request body identifier if present
                currentLine = currentLine.replace(MitmProxyRunner.REQUEST_BODY_IDENTIFIER, "");
                //if current line doesn't signify end of sequence, add previousLine as we are in response body
                httpMessage.requestBody.putAll(parseRequestBody(currentLine).requestBody);
            }

        }

        return results;

    }

    private boolean httpSequenceEnd(String s) {
        //Look for << followed by a space and a 3 digit number starting with 1-5. This denotes the HTTP status code line
        final String regex = ".* << [1-5][0-9][0-9] .*";
        final String serverConnectionTimeout = ".*<< Server connection.*\\[Errno 60].*";
        //It might be better to rework this and match off of the IP address in the line before this (in the http message), but there are risks with that approach as well.
        //I believe these to be the only two things to look for but if there are more it is worth refactoring.
        return s.matches(regex) || s.matches(serverConnectionTimeout);
    }

    /**
     * Gets all Http messages with requests in the body
     *
     * @return a list of all request bodies or null if none is found
     */
    public List<HttpMessage> getAllRequestBodies(List<HttpMessage> httpRequests) {
        List<HttpMessage> requestBodies = new ArrayList<>();

        for (HttpMessage httpMessage : httpRequests) {
            if(!httpMessage.requestBody.isEmpty()) {
                requestBodies.add(httpMessage);
            }

        }

        if(requestBodies.isEmpty()) {
            return null;
        } else {
            return requestBodies;
        }
    }

    public HttpMessage parseRequestBody(String responseBody) {
        List<String> unseparatedKVPairs =  Arrays.asList(responseBody.split("&"));
        HttpMessage result = new HttpMessage();

        for(String s : unseparatedKVPairs) {
            //This will still be ok if there is no value, the split will just do nothing.
            String[] singleKV = s.split("=");
            if(singleKV.length > 2 ) {
                Logger.log("need logic to handle headers");
                result.requestBody.put(s, "");
            } else if(singleKV.length == 2) {
                result.requestBody.put(singleKV[0], singleKV[1]);
            } else {
                result.requestBody.put(singleKV[0], "");
            }
        }

        if(result.requestBody.isEmpty()) {
            return null;
        } else {
            return result;
        }
    }

    public List<HttpMessage> parseRequestBodies(List<String> requestBodies) {
        List<HttpMessage> parsedBodies = new ArrayList<>();
        for (String s : requestBodies) {
            HttpMessage parsedRequest = parseRequestBody(s);
            if (!parsedRequest.requestBody.isEmpty()) {
                parsedBodies.add(parsedRequest);
            }
        }

        if (parsedBodies.isEmpty()) {
            return null;
        } else {
            return parsedBodies;
        }

    }


}
