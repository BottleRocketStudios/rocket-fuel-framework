package com.bottlerocket.domod;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Class which holds time and timeunit
 *
 * Created by ford.arnett on 9/1/15.
 */
public class WaitUnit {
    public long time;
    public TimeUnit timeUnit;

    public WaitUnit(long l, TimeUnit timeUnit) {
        time = l;
        this.timeUnit = timeUnit;
    }

    public Duration getDuration() {
        return Duration.of(time, timeUnit.toChronoUnit());
    }
}
