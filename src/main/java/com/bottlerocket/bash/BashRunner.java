package com.bottlerocket.bash;

import com.bottlerocket.utils.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Manage the execution and lifecycle of a bash command on the terminal. Currently a new runner instance is expected for each command run.
 */
public class BashRunner {
    private final String redirectErrorLocation;
    private final String redirectOutputLocation;
    private Process runningProcess;
    private List<String> errorStream;
    private List<String> inputStream;
    private String dumpOutputPath;

    public BashRunner(String redirectOutputLocation, String redirectErrorLocation) {
        this.redirectOutputLocation = redirectOutputLocation;
        this.redirectErrorLocation = redirectErrorLocation;
    }

    private void logTerminalCommand(String command) {
        Logger.log("Sending command to terminal " + command);
    }

    private void logTerminalCommand(List<String> command) {
        StringBuilder out = new StringBuilder();
        for (String s : command) {
            out.append(" ").append(s);
        }
        Logger.log("Sending command to terminal " + out);
    }

    public void executeCommand(BashCommand command, boolean waitFor, String redirectOutputLocation, String redirectErrorLocation, boolean log) throws IOException, InterruptedException {
        ArrayList<String> bashCommand = command.buildCommand();

        if(log) {
            logTerminalCommand(bashCommand);
        }
        ProcessBuilder processBuilder = new ProcessBuilder(bashCommand);
        processBuilder.redirectOutput(new File(redirectOutputLocation));
        processBuilder.redirectError(new File(redirectErrorLocation));
        runningProcess = processBuilder.start();
        if (waitFor) {
            runningProcess.waitFor();
        }

        //Responses from process
        errorStream = getStreamAsArray(runningProcess.getErrorStream());
        inputStream = getStreamAsArray(runningProcess.getInputStream());
    }

    public void executeCommand(BashCommand command, boolean waitFor) throws IOException, InterruptedException {
        executeCommand(command, waitFor, redirectOutputLocation, redirectErrorLocation, true);
    }

    public void executeCommand(BashCommand command, boolean waitFor, boolean log) throws IOException, InterruptedException {
        executeCommand(command, waitFor, redirectOutputLocation, redirectErrorLocation, log);
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

    public void stopProcess(){
        runningProcess.destroy();
        logTerminalCommand("destroy process");
    }

    public List<String> getErrorStream() {
        return errorStream;
    }

    public List<String> getInputStream() {
        return inputStream;
    }

    public boolean isRunning() {
        return runningProcess.isAlive();
    }


}