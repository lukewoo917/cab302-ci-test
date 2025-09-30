package com.example.demo.model;

import com.example.demo.util.CountdownTimer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

class SessionTest {

    // Resets session to an empty state after each test
    @AfterEach
    void resetSession() {
        Session.clear();
    }

    @Nested
    class UserSessionTests {

        // Checks when you assign a user to the session, getUser() returns the same user
        @Test
        void setAndGetUser_returnsSameUser() {
            User user = new User("Alice", "alice@example.com", "pw".getBytes(), "salt".getBytes());
            Session.setUser(user);

            assertEquals("Alice", Session.getUser().getUsername());
            assertEquals("alice@example.com", Session.getUser().getEmail());
        }

        // Ensures session reset clears the logged-in user
        @Test
        void clear_removesUser() {
            Session.setUser(new User("Bob", "bob@example.com", "pw".getBytes(), "salt".getBytes()));
            Session.clear();

            assertNull(Session.getUser());
        }
    }

    @Nested
    class ReadingSessionTests {

        // Confirms current reading is stored and retrieved properly
        @Test
        void setAndGetReading_returnsSameReading() {
            Reading reading = new Reading(null, "Title", "Content", List.of(), 1 );
            Session.setCurrentReading(reading);

            assertEquals("Title", Session.getCurrentReading().title());
        }

        // Ensures readings are reset with session clear
        @Test
        void clear_removesReading() {
            Session.setCurrentReading(new Reading(null,"T", "C", List.of(), 1));
            Session.clear();

            assertNull(Session.getCurrentReading());
        }
    }

    @Nested
    class QuizResultTests {

        // Ensures quiz results are stored and retrieved properly
        @Test
        void setAndGetQuizResult_returnsSameResult() {
            // array list instead of normal list
            QuizResult qr = new QuizResult(10, 2, 2, List.of(), List.of(), List.of());
            Session.setLastQuizResult(qr);

            assertEquals(10, Session.getLastQuizResult().score());
        }

        // Ensures results can be cleared independently of whole session
        @Test
        void clearLastQuizResult_setsNull() {
            Session.setLastQuizResult(new QuizResult(5, 1, 1, List.of(), List.of(), List.of()));
            Session.clearLastQuizResult();

            assertNull(Session.getLastQuizResult());
        }
    }

    @Nested
    class TimerSessionTests {

        CountDownLatch finished = new CountDownLatch(1);

        // Confirms timer is stored and retrieved properly
        @Test
        void setAndGetGameTimer_returnsSameTimer() {
            CountdownTimer timer = new CountdownTimer(30, secs -> {}, finished::countDown);
            Session.setGameTimer(timer);

            assertSame(timer, Session.getGameTimer());
        }

        // Ensures timer can be cleared
        @Test
        void clearGameTimer_setsNull() {
            CountdownTimer timer = new CountdownTimer(60, secs -> {}, finished::countDown);
            Session.setGameTimer(timer);
            Session.clearGameTimer();

            assertNull(Session.getGameTimer());
        }

        // Ensures clearing quiz result does not affect timer
        @Test
        void clearLastQuizResult_doesNotAffectTimer() {
            CountdownTimer timer = new CountdownTimer(45, secs -> {}, finished::countDown);
            Session.setGameTimer(timer);

            Session.setLastQuizResult(new QuizResult(10, 2, 2, List.of(), List.of(), List.of()));
            Session.clearLastQuizResult();

            assertSame(timer, Session.getGameTimer());
        }

        // Ensures clearing session clears timer as well
        @Test
        void clearSession_removesTimer() {
            CountdownTimer timer = new CountdownTimer(15, secs -> {}, finished::countDown);
            Session.setGameTimer(timer);

            Session.clear();

            assertNull(Session.getGameTimer());
        }
    }

}