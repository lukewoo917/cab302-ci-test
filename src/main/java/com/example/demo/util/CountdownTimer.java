package com.example.demo.util;

import com.example.demo.exceptions.TimerAlreadyRunningException;
import com.example.demo.exceptions.TimerNotRunningException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import java.util.function.Consumer;


/**
 * Simple countdown timer
 * <p>
 * The timer ticks every second on the JavaFX Application Thread
 * so UI updates can be performed directly in the {@code onTick} callback
 * When the countdown reaches 0, the {@code onFinished} callback is invoked
 */
public class CountdownTimer {
    private final int totalSeconds;
    private int remaining;
    private Consumer<Integer> onTick; // called with remaining seconds;
    private Runnable onFinished; // called when hitting 0
    private Timeline timeline;
    private double rate = 1.0;


    /**
     * Constructs a new {@code CountdownTimer}.
     *
     * @param totalSeconds the total duration of the countdown in seconds (must be > 0).
     * @param onTick       a callback that is invoked every second with the remaining time in seconds.
     *                     Can be {@code null} if no per-second action is needed.
     * @param onFinished   a callback that is invoked once when the countdown reaches zero.
     *                     Can be {@code null} if no final action is needed.
     * @throws IllegalArgumentException if {@code totalSeconds} is zero or negative.
     */

    public CountdownTimer(int totalSeconds, Consumer<Integer> onTick, Runnable onFinished) {

        if (totalSeconds <= 0) {
            throw new IllegalArgumentException("Time must run for at least 1 second.");
        }

        this.totalSeconds = totalSeconds;
        this.remaining = totalSeconds;
        this.onTick = onTick;
        this.onFinished = onFinished;
    }


    /**
     * Sets the playback rate for the timer.
     * 1.0 = normal speed, 2.0 = double speed, 60.0 = 60x faster, etc.
     * Can be called before or after start()
     */
    public void setRate(double rate)
    {
        this.rate = rate;
        if (timeline != null) {
            timeline.setRate(rate);
        }
    }


    /**
     * Starts the countdown from the beginning.
     * <p>
     * If the timer is already running, it will be stopped and restarted.
     */
    public void start() throws TimerAlreadyRunningException{
        if (timeline != null)
        {
            throw new TimerAlreadyRunningException("Timer is already running");
        }

        remaining = totalSeconds;

        timeline = new Timeline(
                new KeyFrame(Duration.ZERO, e -> tick()),
                new KeyFrame(Duration.seconds(1))
        );

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.setRate(rate);
        timeline.playFromStart();
    }

    /**
     * Stops the countdown if it is running.
     * <p>
     * Safe to call multiple times — if the timer is not running, nothing happens.
     */
    public void stop() {
        if (timeline == null) {
            throw new TimerNotRunningException("Cannot stop: timer is not running");
        }

        timeline.stop();
        timeline = null;
    }


    /**
     * Checks if a timer is running
     */
    public boolean isRunning() {
        return timeline != null;
    }


    // Allow controllers to attach/replace callbacks when they show
    public void setOnTick(Consumer<Integer> onTick) {
        this.onTick = onTick;
    }

    public void setOnFinished(Runnable onFinished) {
        this.onFinished = onFinished;
    }


    /**
     * Pauses the countdown if it is running.
     * <p>
     * Has no effect if the timer is not running.
     */
    public void pause() {
        if (timeline == null) {
            throw new TimerNotRunningException("Cannot pause: timer is not running");
        }

        timeline.pause();

    }

    /**
     * Resumes the countdown if it was paused.
     * <p>
     * Has no effect if the timer is not running or already playing.
     */
    public void resume() {
        if (timeline == null) {
            throw new TimerNotRunningException("Cannot resume: timer is not running");
        }

        timeline.play();
    }


    /**
     * Returns the number of seconds remaining until the timer finishes.
     *
     * @return remaining time in seconds (0 if the countdown has completed).
     */
    public int getRemainingSeconds() {
        return Math.max(remaining, 0);
    }

    /** Internal method called once per second to update the remaining time
     * <p>
     * Executes the {@code onTick} callback and checks if the timer has finished
     */
    private void tick() {
        if (onTick != null) {
            onTick.accept(remaining);
        }

        if (remaining <= 0) {
            stop();
            if (onFinished != null) {
                onFinished.run();
            }
        }

        remaining--;
    }


}
