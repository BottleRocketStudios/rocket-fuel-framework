package com.bottlerocket.mockserver.mockloader;

import com.bottlerocket.config.MockLoaderConfigurations;
import com.bottlerocket.utils.Logger;
import io.appium.mitmproxy.InterceptedMessage;

import java.io.*;

import static com.bottlerocket.config.MockLoaderConfigurations.expectationsFilePath;

public class MockParser {

    //TODO: The client may make a call with an identical path to separate hosts. MockServer can't handle this well. A solution must be proposed.
    //TODO: create accept headers for application/json type only, we don't want HTML in the initializer
    //TODO: regex is escaping already escaped json characters. Tell it not to do this.

    private static File expectationsFile = new File(MockLoaderConfigurations.expectationsFilePath);

    private static void writeJsontoFile(String message){
        try{
            FileWriter expectationsFileWriter = new FileWriter(expectationsFilePath, true);
            expectationsFileWriter.write(message);
            expectationsFileWriter.close();
        } catch (Exception e){
            Logger.log("Failed to write JSON message to file.", "mockloader");
        }
    }
    private static void writeExpectationBlock(String requestJson, String responseJson){
        try {
            if (checkIfJsonFileEmpty()) { writeJsontoFile("["); } else { prepareForAppend(); }
        } catch (Exception e) {
            Logger.log("Please check provided ExceptionInit.Json filepath.", "mockloader");
        }
        writeJsontoFile(requestJson);
        writeJsontoFile(responseJson);
        writeJsontoFile("]");
    }

    private static boolean checkIfJsonFileEmpty() throws FileNotFoundException {
        if (expectationsFile.exists()) {
            return (expectationsFile.length() == 0);
        } else {
            throw new FileNotFoundException(expectationsFilePath);
        }
    }

    private static void prepareForAppend(){
        executeBashCommand("sed -i.bak $s/.$// "+expectationsFilePath);
        try {
            Thread.sleep(100);
        } catch (Exception e){
            Logger.log("Sed is terrible. "+e, "mockloader");
        }
        writeJsontoFile(",");
    }

    public static boolean checkIfFileExists(String path){
        File tempFile = new File(path);
        return tempFile.exists();
    }

    // framework bash package is impossible to use, substituting for simplicity
    public static void executeBashCommand(String exec){
        StringBuffer output = new StringBuffer();

        Process p;
        try {
            p = Runtime.getRuntime().exec(exec);
            p.waitFor();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine())!= null) {
                output.append(line + "\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void generateJsonObj(InterceptedMessage message){
        // following regex removes "http://", "https://", the top-level domain, and everything in between.
        // reason for this - MockServer only needs the request path at the current time. may change in the future.
        String requestPath = message.getRequest().getUrl().replaceAll("(https?\\:\\/\\/([a-z,.a-z,.a-z]*))", "").split("\\?")[0];
        // certain characters need to be escaped from the json body, as the body is stored as a string inside a json object
        String responseBody = new String(message.getResponse().getBody()).replace("\"","\\\"").replace("\n", "\\n");
        String requestJson = "{\"httpRequest\": {\"path\": \""+requestPath+"\"},";
        String responseJson = "\"httpResponse\": {\"body\": \""+ responseBody+"\"}}";
        writeExpectationBlock(requestJson, responseJson);
    }
}
