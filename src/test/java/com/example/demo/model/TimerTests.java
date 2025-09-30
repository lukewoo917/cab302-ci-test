package com.example.demo.model;

import com.example.demo.exceptions.TimerAlreadyRunningException;
import com.example.demo.exceptions.TimerNotRunningException;
import com.example.demo.util.CountdownTimer;
import javafx.application.Platform;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class CountdownTimerTest {
    private static boolean fxStarted = false;

    private CountdownTimer timer;

    /** JUnit tests that use Timeline or any JavaFX API need the JavaFX toolkit started first
     */
    @BeforeAll
    static void initJavaFx() throws Exception{
        if (!fxStarted) {
            try {
                CountDownLatch latch = new CountDownLatch(1);
                Platform.startup(latch::countDown);
                latch.await(5, TimeUnit.SECONDS);
            } catch (IllegalStateException e) {
                // JavaFX already running
            }
            Platform.setImplicitExit(false);
            fxStarted = true;
        }
    }

    /** Stop timer after each test so it doesn't interfere with the next test
     *
     */
    @AfterEach
    void cleanUp() {
        if (timer != null) {
            try {
                timer.stop();
                timer.setRate(1.0);
            } catch (Exception ignored) {
                // already stopped or not running - okay
            }
        }
    }

    @Nested
    class CountdownTimerConstructorValidation {
        @Test
        void timerDurationZeroOrNegativeDurationThrowsException()
        {
            assertThrows(IllegalArgumentException.class, () -> new CountdownTimer(0, null, null));
            assertThrows(IllegalArgumentException.class, () -> new CountdownTimer(-5, null, null));
        }
    }



    @Nested
    class CountdownTimerLifeCycle {
        @Test
        void timerCannotStartTwiceThrowsException() {
            timer = new CountdownTimer(1, null, null);
            timer.start();
            assertThrows(TimerAlreadyRunningException.class, () -> timer.start());
        }

        @Test
        void timerStopWhenNotRunningThrowsException() {
            timer = new CountdownTimer(1, null, null);
            assertThrows(TimerNotRunningException.class, () -> timer.stop());
        }

        @Test
        void timerPauseWhenNotRunningThrowsException() {
            timer = new CountdownTimer(1, null, null);
            assertThrows(TimerNotRunningException.class, () -> timer.pause());
        }

        @Test
        void timerResumeWhenNotRunningThrowsException() {
            timer = new CountdownTimer(1, null, null);
            assertThrows(TimerNotRunningException.class, () -> timer.resume());
        }
    }


    @Nested
    class CountdownTimerBehaviour {
        @Test
        void timerCountsDownAndFiresFinished() throws Exception {
            CountDownLatch finished = new CountDownLatch(1);
            StringBuilder ticks = new StringBuilder();

            timer = new CountdownTimer(
                    1,
                    secs -> ticks.append(secs).append(','),
                    finished::countDown
            );

            timer.start();

            // 1-second logical timer -> allow up to 2s wall time as buffer
            assertTrue(finished.await(2, TimeUnit.SECONDS), "Timer did not finish in time");

            // Expect tick at 1 and 0 (0 happens just before finish)
            assertTrue(ticks.toString().contains("1,"), "Missing tick at 1: " + ticks);
            assertTrue(ticks.toString().contains("0,"), "Missing tick at 0: " + ticks);
        }

        @Test
        void timerPauseAndResumeHoldsThenCompletes() throws Exception {
            CountDownLatch finished = new CountDownLatch(1);
            final int[] last = { -1 };

            timer = new CountdownTimer(
                    1,
                    secs -> last[0] = secs, // observe remaining value
                    finished::countDown
            );

            timer.start();

            // Let one tick happen (down to 0) - but timing jitter can occur around boundary
            // Sleep a bit less than a second so we likely pause at remaining = 0 or 1
            Thread.sleep(300);
            timer.pause();
            int snapshot = last[0];

            // While paused, value should not change
            Thread.sleep(300);
            assertEquals(snapshot, last[0], "Remaining changed while paused");

            // Resume and allow it to finish
            timer.resume();
            assertTrue(finished.await(2, TimeUnit.SECONDS), "Timer did not finish after resume");
        }
    }
}


//class SessionSharedTimerTest {
//    private static boolean fxStarted = false;
//
//    @BeforeAll
//    static void startFx() throws Exception {
//        if (!fxStarted) {
//            try {
//                CountDownLatch latch = new CountDownLatch(1);
//                Platform.startup(latch::countDown);
//                latch.await(5, TimeUnit.SECONDS);
//            } catch (IllegalStateException e) {
//                // JavaFX already running
//            }
//            fxStarted = true;
//        }
//    }
//
//    @AfterEach
//    void cleanUp() {
//        Session.stopAndClearGameTimer();
//    }
//
//    @Test
//    void startGameTimerIfNeededCreatesAndStartsTimerAndHonorsDebugRate() throws Exception {
//        // Force a fast rate during this test by toggling debug flag if you added one.
//        Session.startGameTimerIfNeeded();
//        CountdownTimer t1 = Session.getGameTimer();
//
//        assertNotNull(t1, "Shared timer should be created");
//        assertTrue(t1.isRunning(), "Shared timer should be running");
//
//        // Attach a quick finished latch so the test doesn't run forever
//        CountDownLatch finished = new CountDownLatch(1);
//        t1.setOnFinished(finished::countDown);
//        t1.setRate(240.0); // accelerate here to make the test complete fast
//
//        assertTrue(finished.await(1050, TimeUnit.MILLISECONDS),
//                "Shared timer did not finish when accelerated");
//    }
//
//    @Test
//    void timerFinishesQuicklyAtHighRate() throws Exception {
//        CountDownLatch finished = new CountDownLatch(1);
//        CountdownTimer t = new CountdownTimer(10, null, finished::countDown);
//        t.setRate(30.0); // 10s logical should finish ~0.33s
//        long t0 = System.nanoTime();
//        t.start();
//        assertTrue(finished.await(2, TimeUnit.SECONDS), "did not finish");
//        long ms = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - t0);
//        assertTrue(ms < 1500, "finished too slowly at high rate: " + ms + "ms");
//    }
//
//
//    @Test
//    void startGameTimerIfNeededReusesExistingTimerInsteadOfCreatingNew() {
//        Session.startGameTimerIfNeeded();
//        CountdownTimer first = Session.getGameTimer();
//        assertNotNull(first);
//
//        Session.startGameTimerIfNeeded();       // should reuse
//        CountdownTimer second = Session.getGameTimer();
//
//        assertSame(first, second, "Session should reuse the same shared timer instance");
//    }
//
//
//    @Test
//    void sharedTimerCarriesRemainingBetweenPhasesAndAllowsReattach() throws Exception {
//        // Start a fresh 10s logical timer at a moderate fast rate (avoid ultra-fast jitter)
//        Session.stopAndClearGameTimer();
//
//        // Attach Phase 1 listener BEFORE start to capture the first ticks reliably
//        AtomicInteger lastP1 = new AtomicInteger(-1);
//        CountDownLatch sawTen = new CountDownLatch(1);
//        CountDownLatch sawBelowTen = new CountDownLatch(1);
//
//        CountdownTimer t = new CountdownTimer(
//                10,
//                secs -> {
//                    lastP1.set(secs);
//                    if (secs == 10) sawTen.countDown();
//                    if (secs < 10)  sawBelowTen.countDown();
//                },
//                null // we won't depend on this callback for the assertion
//        );
//
//        // (Optional) put into Session if your app does that
//        SessionTestHooks.setGameTimer(t);
//
//        t.setRate(20.0);    // not too high — reduces CI/FX jitter but still fast
//        t.start();
//
//        // Wait for first tick at 10, then any tick below 10
//        assertTrue(sawTen.await(1, TimeUnit.SECONDS), "No first tick at 10");
//        assertTrue(sawBelowTen.await(2, TimeUnit.SECONDS), "No tick below 10");
//        int snapshot = lastP1.get();
//        assertTrue(snapshot <= 9, "Expected remaining < 10; got " + snapshot);
//
//        // ---- Phase switch: reattach UI like QuestionsController.initialize ----
//        AtomicInteger lastP2 = new AtomicInteger(-1);
//        t.setOnTick(lastP2::set);
//        // DON'T rely on onFinished latch here; it's easy to miss if the timer finishes now.
//
//        // Prove the timer eventually finishes by state: either it stops or remaining==0
//        long deadlineNanos = System.nanoTime() + TimeUnit.SECONDS.toNanos(4);
//        boolean finishedByState = false;
//        while (System.nanoTime() < deadlineNanos) {
//            if (!t.isRunning() || t.getRemainingSeconds() <= 0) {
//                finishedByState = true;
//                break;
//            }
//            Thread.sleep(10);
//        }
//        assertTrue(finishedByState, "Shared timer did not finish after reattach (state check)");
//
//        // If reattached ticks were observed, they must not go *up*
//        if (lastP2.get() != -1) {
//            assertTrue(lastP2.get() <= snapshot, "Remaining increased after reattach: " + lastP2.get() + " > " + snapshot);
//        }
//    }
//
//
//    /**
//     * Minimal test-only hook to replace the session timer (since Session keeps it private)
//     * Only for testing
//     */
//    static class SessionTestHooks {
//        static void setGameTimer(CountdownTimer timer) {
//            try {
//                var field = Session.class.getDeclaredField("gameTimer");
//                field.setAccessible(true);
//                field.set(null, timer);
//            } catch (Exception e) {
//                fail("Failed to inject test timer into Session: " + e);
//            }
//        }
//    }
//}


class CountdownTimerRateTest {

    private static boolean fxStarted = false;
    private CountdownTimer timer;

    @BeforeAll
    static void startFx() throws Exception {
        if (!fxStarted) {
            try {
                CountDownLatch latch = new CountDownLatch(1);
                Platform.startup(latch::countDown);
                latch.await(5, TimeUnit.SECONDS);
            } catch (IllegalStateException e) {
                // JavaFX already running
            }
            Platform.setImplicitExit(false);
            fxStarted = true;
        }
    }

    @AfterEach
    void cleanup() {
        if (timer != null) {
            try { timer.stop(); } catch (Exception ignored) {}
        }
    }

    @Test
    void setRateBeforeStartIsAppliedOnStart() throws Exception {
        CountDownLatch finished = new CountDownLatch(1);

        timer = new CountdownTimer(
                30,                   // 30 logical seconds
                secs -> {},           // ignore ticks
                finished::countDown
        );

        timer.setRate(120.0);      // 60× faster → ~0.5s wall time
        timer.start();

        assertTrue(finished.await(1, TimeUnit.SECONDS),
                "Timer did not finish at accelerated speed when set before start()");
    }

    @Test
    void setRateAfterStartChangesSpeed() throws Exception {
        CountDownLatch finished = new CountDownLatch(1);

        timer = new CountdownTimer(30, secs -> {}, finished::countDown);
        timer.start();            // starts at 1× by default

        Thread.sleep(100);        // tiny delay to ensure it actually started
        timer.setRate(120.0);      // now accelerate a lot

        assertTrue(finished.await(1, TimeUnit.SECONDS),
                "Timer did not finish after increasing rate at runtime");
    }

    @Test
    void emitsAllTicksInDescendingOrderAndCallsFinishedOnce() throws Exception {
        final int total = 10;
        final StringBuilder seen = new StringBuilder();
        final AtomicInteger last = new AtomicInteger(total + 1);
        final AtomicInteger finishedCount = new AtomicInteger(0);

        CountDownLatch done = new CountDownLatch(1);

        timer = new CountdownTimer(
                total,
                secs -> {
                    int prev = last.getAndSet(secs);
                    if (prev != total + 1) {
                        assertEquals(prev - 1, secs, "Tick order broken");
                    }
                    seen.append(secs).append(',');
                },
                () -> {
                    finishedCount.incrementAndGet();
                    done.countDown();
                }
        );

        timer.setRate(120.0);    // very fast
        timer.start();

        assertTrue(done.await(1, TimeUnit.SECONDS), "Finished was not called in time");
        assertTrue(seen.indexOf("10,") == 0, "First tick should be 10: " + seen);
        assertTrue(seen.toString().contains("0,"), "Missing tick at 0: " + seen);
        assertEquals(1, finishedCount.get(), "Finished should be called exactly once");
    }
}