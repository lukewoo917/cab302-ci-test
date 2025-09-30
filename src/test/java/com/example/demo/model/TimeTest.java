package com.example.demo.model;


import com.example.demo.exceptions.TimerAlreadyRunningException;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;



class TimeTest {

    private static boolean fxStarted = false;

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

    //Constructor validation. Must be >0
    @Test
    void constructor_zeroOrNegative_throws(){
        assertThrows(IllegalArgumentException.class,()-> new CountdownTimer(0,null,null));
        assertThrows(IllegalArgumentException.class,()-> new CountdownTimer(-1,null,null));
    }

    //Starts, runs and finishes. OnFinished called, not running at end.
    @Test
    void start_runsToCompletion_triggersOnFinished()throws Exception{
        CountDownLatch finished = new CountDownLatch(1);
        CountdownTimer t = new CountdownTimer(2,null,finished::countDown);

        t.setRate(100);//Speed up testing
        t.start();

        assertTrue(t.isRunning());

        assertTrue(finished.await(200,TimeUnit.MILLISECONDS));

        assertEquals(0,t.getRemainingSeconds());

        assertFalse(t.isRunning());

    }

    //OnTick is called immediately and then each second.
    @Test
    void OnTick_calledImmediatelyAndEachSecond() throws Exception{
        AtomicInteger ticks = new AtomicInteger(0);
        CountDownLatch finished = new CountDownLatch(1);

        CountdownTimer t = new CountdownTimer(2,r->ticks.incrementAndGet(),finished::countDown);
        t.setRate(100);
        t.start();

        assertTrue(finished.await(200,TimeUnit.MILLISECONDS));
        //For 2 seconds, expect ticks at 2,1,0. 3 total
        assertEquals(3,ticks.get(),"Expected ticks at 2,1,0");
    }

    //Double start not allowed
    @Test
    void start_whenAlreadyRunning_throws() throws Exception{
        CountdownTimer t = new CountdownTimer(2,null,null);
        t.setRate(50);
        t.start();
        assertThrows(TimerAlreadyRunningException.class,t::start);
    }


    //Stop while running. Timer stops.
    @Test
    void stop_stopsTimer_capturesRemainingSeconds() throws Exception{
        CountDownLatch stopped=new CountDownLatch(1);

        //Created so we can reference it inside lambda
        CountdownTimer[] holder = new CountdownTimer[1];

        CountdownTimer t = new CountdownTimer(
                3,
                rem->{
                    if (rem==2) {
                    //Run stop()
                        javafx.application.Platform.runLater(()->{
                            holder[0].stop();
                            stopped.countDown();
                        });
                    }
                },
                null
        );

        holder[0]=t;
        t.setRate(100);
        t.start();

        assertTrue(stopped.await(250, TimeUnit.MILLISECONDS),"Timer did not stop in time");
        assertFalse(t.isRunning(),"Timer should be stopped");
        assertEquals(1,t.getRemainingSeconds(),"Expected remaining to be 1 after stopping post 2 ticks");
    }

}
