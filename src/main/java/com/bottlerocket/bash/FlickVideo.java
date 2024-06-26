package com.bottlerocket.bash;

public class FlickVideo {
    //Not an exhaustive list, see flick for more
    public static final String VIDEO_COMMAND = "video";
    public static final String INFO_COMMAND = "info";
    public static final String LOG_COMMAND = "log";
    public static final String MANAGER_COMMAND = "manager";
    public static final String VITALS_COMMAND = "vitals";

    FlickVideo(String action, String platform, String udid, double seconds, String count, boolean extend, String name, boolean unique, String format, String outdir) {
        this.action = action;
        this.platform = platform;
        this.udid = udid;
        this.seconds = seconds;
        this.count = count;
        this.extend = extend;
        this.name = name;
        this.unique = unique;
        this.format = format;
        this.outdir = outdir;
    }

    String action;
    String platform;
    String udid;
    double seconds;
    String count;
    boolean extend;
    String name;
    boolean unique;
    String format;
    String outdir;


    public enum FLICK_PARAMS {
        action("-a"),
        platform("-p"),
        udid("-u"),
        seconds("-s"),
        count("-c"),
        extend("-e"),
        name("-n"),
        unique("-q"),
        format("-f"),
        outdir("-o");

        String paramFlag;

        FLICK_PARAMS(String paramFlag) {
            this.paramFlag = paramFlag;
        }

        @Override
        public String toString() {
            return paramFlag;
        }
    }




}
