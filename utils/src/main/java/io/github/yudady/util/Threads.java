package io.github.yudady.util;

import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author neo
 */
public final class Threads {
    private static final Logger LOGGER = LoggerFactory.getLogger(Threads.class);

    public static void sleepRoughly(Duration duration) {
        long sleepTime = sleepTime(duration);
        LOGGER.debug("sleep {} ms", sleepTime);
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            LOGGER.debug("sleep is interrupted", e);
        }
    }

    static long sleepTime(Duration duration) {
        long milliseconds = duration.toMillis();
        double times = Randoms.nextDouble(0.8, 1.2); // +/-20% random
        return (long) (milliseconds * times);
    }
}
