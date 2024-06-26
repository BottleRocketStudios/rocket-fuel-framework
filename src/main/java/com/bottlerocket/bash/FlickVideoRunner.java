package com.bottlerocket.bash;

import com.bottlerocket.config.AutomationConfigProperties;
import com.bottlerocket.config.AutomationConfigPropertiesLoader;

import java.io.File;
import java.io.IOException;

/**
 * Contains methods for basic start/stop video recording.
 * <br><br>
 * In order to do something more customizable, use {@link FlickVideoBuilder} to create a {@link FlickVideo} object with your specifications.
 * Then use {@link #runVideoCommand(FlickVideo, String)} to actually run your {@link FlickVideo} instance.
 *
 */
public class FlickVideoRunner {

    private File bashDebugOut;

    public FlickVideoRunner(File bashDebugOut) {
        this.bashDebugOut = bashDebugOut;
    }

    public void startVideo(AutomationConfigProperties properties) throws IOException, InterruptedException {
        FlickVideoBuilder builder = new FlickVideoBuilder(FlickVideo.VIDEO_COMMAND);
        //This obviously needs help if there is ever a need for more supported operating systems
        String os = properties.isAndroid() ? "android" : "ios";
        FlickVideo videoRunner = builder.setAction("start")
                .setPlatform(os)
                .setUdid(properties.udid)
                .setExtend(true)
                .createFlickVideoRunner();

        runVideoCommand(videoRunner, FlickVideo.VIDEO_COMMAND);
    }

    public void stopVideo(AutomationConfigProperties properties) throws IOException, InterruptedException {
        FlickVideoBuilder builder = new FlickVideoBuilder(FlickVideo.VIDEO_COMMAND);
        //Create directory for video to live
        new File(properties.screenRecordDirectory).mkdirs();

        //This obviously needs help if there is ever a need for more supported operating systems
        String os = properties.isAndroid() ? "android" : "ios";
        FlickVideo flickVideo = builder.setAction("stop")
                .setPlatform(os)
                .setUdid(properties.udid)
                .setOutdir(properties.screenRecordDirectory)
                .createFlickVideoRunner();

        runVideoCommand(flickVideo, FlickVideo.VIDEO_COMMAND);
    }

    public void runVideoCommand(FlickVideo video, String flickCommand) throws IOException, InterruptedException {
        //add flick, then the second part of the command
        BashCommand command = bindToBashCommand(video, "flick " + flickCommand);
        BashRunner bashRunner = new BashRunner(bashDebugOut + "/flick_out", bashDebugOut + "/flick_err");

        bashRunner.executeCommand(command, true);
    }

    private BashCommand bindToBashCommand(FlickVideo video, String commandString) {
        BashCommand command = new BashCommand(commandString);

        setParamPair(command, FlickVideo.FLICK_PARAMS.action, video.action);
        setParamPair(command, FlickVideo.FLICK_PARAMS.platform, video.platform);
        setParamPair(command, FlickVideo.FLICK_PARAMS.udid, video.udid);
        setParamPair(command, FlickVideo.FLICK_PARAMS.seconds, String.valueOf(video.seconds));
        setParamPair(command, FlickVideo.FLICK_PARAMS.count, video.count);
        setParamPair(command, FlickVideo.FLICK_PARAMS.extend, String.valueOf(video.extend));
        setParamPair(command, FlickVideo.FLICK_PARAMS.name, video.name);
        setParamPair(command, FlickVideo.FLICK_PARAMS.unique, String.valueOf(video.unique));
        setParamPair(command, FlickVideo.FLICK_PARAMS.format, video.format);
        setParamPair(command, FlickVideo.FLICK_PARAMS.outdir, video.outdir);

        return command;
    }

    private void setParamPair(BashCommand command, FlickVideo.FLICK_PARAMS paramFlag, String paramValue) {
        if(paramValue != null && !paramValue.isEmpty()) {
            command.addParam(paramFlag.toString(), paramValue);
        }
    }
}
