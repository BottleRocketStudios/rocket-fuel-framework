package com.bottlerocket.httphandler.mitmproxy;

import com.bottlerocket.bash.BashCommand;
import com.bottlerocket.bash.BashRunner;
import com.bottlerocket.config.AutomationConfigProperties;
import com.bottlerocket.errorhandling.WebDriverWrapperException;
import com.bottlerocket.httphandler.HttpMessage;
import com.bottlerocket.utils.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Runner to interact with the command line tool MITMProxy.
 *
 * For more info see https://mitmproxy.org/
 *
 * Created by ford.arnett on 1/16/17.
 */
public class MitmProxyRunner {
    public static final String REQUEST_BODY_IDENTIFIER = "Request body: ";
    private String mitmProxyHome;
    private BashRunner runner;
    private List<String> errorStream;
    private List<String> inputStream;
    private String dumpOutputPath;

    public MitmProxyRunner(String mitmProxyHome, AutomationConfigProperties configProperties) {
        this.mitmProxyHome = mitmProxyHome;
        dumpOutputPath = configProperties.reportOutputDirectory;
    }

    public MitmProxyRunner() {
        //Use global system mitmProxy
        mitmProxyHome = "";
    }

    private void logTerminalCommand(String command) {
        Logger.log("Sending command to terminal " + command);
    }

    private static List<String> getStreamAsArray(InputStream inputStream) throws IOException {
        String line;
        List<String> result = new ArrayList<>();

        try(BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))){
            while ((line = br.readLine()) != null) {
                result.add(line);
            }
        }

        return result;
    }

    public void startProxy(String sessionName, String redirectOutputLocaiton, String redirectErrorLocation, AutomationConfigProperties configProperties) throws IOException, InterruptedException {
        dumpOutputPath = configProperties.reportOutputDirectory + sessionName;

        //should be something like /User/path/mitmproxy -w User/report_path/sessionName
        BashCommand bashCommand = new BashCommand(mitmProxyHome + "mitmdump");
        bashCommand.addParam("-w", dumpOutputPath);
        runner = new BashRunner(redirectOutputLocaiton, redirectErrorLocation);
        runner.executeCommand(bashCommand, false);

    }

    public void runScriptOnDump(String fullScriptPath) throws IOException {
        //Example command ./mitmdump -n -r aweLaunch -s ../mitm_scripts/example-script.py
        String bashCommand = mitmProxyHome + "mitmdump";

        List<String> commands = new ArrayList<>();
        commands.add(bashCommand);
        //-n don't start server
        commands.add("-n");
        //-r read from file
        commands.add("-r");
        commands.add(dumpOutputPath);
        //-s Run a script, use quotes to add script parameters
        commands.add("-s");
        commands.add("\"" + fullScriptPath + "\"");

        ProcessBuilder processBuilder = new ProcessBuilder(commands);
        Process testRun = processBuilder.start();
        logTerminalCommand(String.join(" ", commands));

        //errorStream = getStreamAsArray(testRun.getErrorStream());
        inputStream = getStreamAsArray(testRun.getInputStream());

    }

    public void stopDump(){
        runner.stopProcess();
    }

    public List<String> getErrorStream() {
        return errorStream;
    }

    public List<String> getInputStream() {
        return inputStream;
    }


    public void printRequestBodyToFile (File f, HttpMessage message) throws WebDriverWrapperException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(f, true))) {
            for (String s : message.requestBody.keySet()) {
                bw.write(s + ", " + message.getRequestBodyValue(s));
                bw.newLine();
            }

        } catch (IOException e) {
            throw new WebDriverWrapperException("Error printing out dump to file", e);
        }
    }

    public void printHttpMessagesToFile(File f, List<HttpMessage> messages) throws WebDriverWrapperException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
            for (HttpMessage message : messages) {
                bw.write(message.call);
                bw.newLine();
                bw.write(message.responseStatus);
                bw.newLine();
                for (String s : message.requestBody.keySet()) {
                    bw.write(s + ", " + message.requestBody.get(s));
                    bw.newLine();
                }
                bw.newLine();
            }

            Logger.log("Printing request to file " + f);

        } catch (IOException e) {
            throw new WebDriverWrapperException("Error printing out dump to file", e);
        }
    }


}
