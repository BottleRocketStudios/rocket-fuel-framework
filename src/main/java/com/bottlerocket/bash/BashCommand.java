package com.bottlerocket.bash;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ford.arnett on 5/24/17.
 */
public class BashCommand {
    private String commandName;
    private List<ParamOptionPair> paramOptPair = new ArrayList<>();
    private Process process;

    public void addParam(ParamOptionPair param) {
        paramOptPair.add(param);
    }

    public void addParam(String option, String param) {
        paramOptPair.add(new ParamOptionPair(option, param));
    }

    public BashCommand(String commandName) {
        this.commandName = commandName;
    }

    public BashCommand(String commandName, List<ParamOptionPair> paramOptPair) {
        this.commandName = commandName;
        this.paramOptPair.addAll(paramOptPair);
    }

    protected ArrayList<String> buildCommand() {
        ArrayList<String> bashCommand = new ArrayList<>();
        //Note this is necessary since some commands are multi word. For example flick video is one command but needs to be separated in the list for it to work properly
        bashCommand.add(commandName);
        for(ParamOptionPair param : paramOptPair) {
            bashCommand.add(param.option);
            bashCommand.add(param.parameter);
        }

        return bashCommand;
    }

    public class ParamOptionPair {
        String option;
        String parameter;

        public ParamOptionPair(String option, String parameter) {
            this.option = option;
            this.parameter = parameter;
        }

        public ParamOptionPair(String parameter) {
            this.parameter = parameter;
        }

        @Override
        public String toString() {
            return option + " " + parameter;
        }



    }
}