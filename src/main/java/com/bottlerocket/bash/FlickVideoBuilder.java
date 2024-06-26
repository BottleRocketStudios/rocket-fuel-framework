package com.bottlerocket.bash;

import java.io.File;

public class FlickVideoBuilder {
    //Currently non string values will be set to the given values here, while if a String is given it will not set that value. This is mainly because it's difficult to tell between an set/unset boolean.
    private String action;
    private String platform;
    private String udid;
    private double seconds = 0.5;
    private String count;
    private boolean extend = true;
    private String name;
    private boolean unique = true;
    private String format;
    private String outdir;
    private String flickCommand;
    private File debugOut;

    public FlickVideoBuilder(String flickCommand) {
        this.flickCommand = flickCommand;
        this.debugOut = debugOut;
    }

    public FlickVideoBuilder setAction(String action) {
        this.action = action;
        return this;
    }

    public FlickVideoBuilder setPlatform(String platform) {
        this.platform = platform;
        return this;
    }

    public FlickVideoBuilder setUdid(String udid) {
        this.udid = udid;
        return this;
    }

    public FlickVideoBuilder setSeconds(double seconds) {
        this.seconds = seconds;
        return this;
    }

    public FlickVideoBuilder setCount(String count) {
        this.count = count;
        return this;
    }

    public FlickVideoBuilder setExtend(boolean extend) {
        this.extend = extend;
        return this;
    }

    public FlickVideoBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public FlickVideoBuilder setUnique(boolean unique) {
        this.unique = unique;
        return this;
    }

    public FlickVideoBuilder setFormat(String format) {
        this.format = format;
        return this;
    }

    public FlickVideoBuilder setOutdir(String outdir) {
        this.outdir = outdir;
        return this;
    }

    public FlickVideo createFlickVideoRunner() {
        return new FlickVideo(action, platform, udid, seconds, count, extend, name, unique, format, outdir);
    }
}