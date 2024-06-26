package com.bottlerocket.utils;

import org.apache.commons.text.TextStringBuilder;
import org.slf4j.helpers.MessageFormatter;

import java.util.ArrayList;
import java.util.List;

public class LogBuilder {
    private final List<String> builder;

    public LogBuilder() {
        builder = new ArrayList<>();
    }

    public LogBuilder appendLine(String message) {
        builder.add(message);
        return this;
    }

    public LogBuilder appendLine(String message, Object... arguments) {
        String formattedMessage = MessageFormatter.arrayFormat(message, arguments).getMessage();
        builder.add(formattedMessage);
        return this;
    }

    public LogBuilder appendLines(List<String> list) {
        builder.addAll(list);
        return this;
    }

    public LogBuilder appendLineBreak() {
        builder.add("");
        return this;
    }

    public LogBuilder appendPrettyLineSeparator() {
        builder.add("-------------------------");
        return this;
    }

    public LogBuilder appendPrettyLineSeparator(Character character, int count) {
        builder.add(String.valueOf(character).repeat(Math.max(0, count)));
        return this;
    }

    public void log() {
        builder.forEach(Logger::log);
        builder.clear();
    }

    public String logAndGetMessage() {
        TextStringBuilder stringBuilder = new TextStringBuilder();
        builder.forEach(stringBuilder::appendln);
        log();
        return stringBuilder.toString();
    }
}