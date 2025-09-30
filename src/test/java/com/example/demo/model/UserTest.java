package com.example.demo.model;

import com.example.demo.controller.QuestionsController;
import com.example.demo.exceptions.TimerAlreadyRunningException;
import com.example.demo.exceptions.TimerNotRunningException;
import com.example.demo.util.CountdownTimer;
import javafx.application.Platform;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    @Nested
    class UsernameValidatorTest {

        @Test
        void nullUsername_returnsError() {
            Optional<List<String>> err = User.verifyUsername(null);
            assertTrue(err.isPresent());
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "a", "ab", "abcd", "abcde"})
            // <=5 chars
        void tooShort_returnsError(String input) {
            Optional<List<String>> err = User.verifyUsername(input);
            assertTrue(err.isPresent());
        }

        @Test
        void tooLong_returnsError() {
            String longName = "a".repeat(51);
            Optional<List<String>> err = User.verifyUsername(longName);
            assertTrue(err.isPresent());
        }

        @ParameterizedTest
        @ValueSource(strings = {"valid_123", "User_1", "A2345_", "abcde_"})
        void validUsernames_areOk(String input) {
            Optional<List<String>> err = User.verifyUsername(input);
            assertTrue(err.isEmpty());
        }

        @ParameterizedTest
        @ValueSource(strings = {"bad-name", "space name", "dot.name", "slash/name", "emoji\uD83D\uDE03, <p>bad</p>"})
        void invalidChars_returnsError(String input) {
            Optional<List<String>> err = User.verifyUsername(input);
            assertTrue(err.isPresent());
        }

        @Test
        void verifyPassword() {
        }
    }

    @Nested
    class PasswordValidatorTest {

        @Test
        void nullPassword_returnsError() {
            Optional<List<String>> err = User.verifyPassword(null);
            assertTrue(err.isPresent());
        }

        @ParameterizedTest
        @ValueSource(strings = {"A1!a", "A1!aa"})
            // too short
        void tooShort_returnsError(String input) {
            Optional<List<String>> err = User.verifyPassword(input);
            assertTrue(err.isPresent());
        }

        @Test
        void tooLong_returnsError() {
            String longPw = "A".repeat(45) + "1!AaA"; // 50 chars
            Optional<List<String>> err = User.verifyPassword(longPw);
            assertTrue(err.isPresent());
        }

        @Test
        void validPassword_isOk() {
            Optional<List<String>> err = User.verifyPassword("Abcdef1!");
            assertTrue(err.isEmpty());
        }

        @Test
        void missingUppercase_returnsError() {
            Optional<List<String>> err = User.verifyPassword("lowercase1!");
            assertTrue(err.isPresent());
        }

        @Test
        void missingDigit_returnsError() {
            Optional<List<String>> err = User.verifyPassword("NoDigits!");
            assertTrue(err.isPresent());
        }

        @Test
        void missingSpecial_returnsError() {
            Optional<List<String>> err = User.verifyPassword("NoSpecial1");
            assertTrue(err.isPresent());
        }

        @ParameterizedTest
        @ValueSource(strings = {"Bad`Char1!", "Space char1 !", "Tab\tChar1!"})
        void invalidCharacters_returnsError(String input) {
            Optional<List<String>> err = User.verifyPassword(input);
            assertTrue(err.isPresent());
        }

        @Test
        void boundaryLength_min_ok() {
            Optional<List<String>> err = User.verifyPassword("A1!aaa");
            assertTrue(err.isEmpty());
        }

        @Test
        void boundaryLength_max_ok() {
            String pw = "Abcd1!" + "a".repeat(43);
            Optional<List<String>> err = User.verifyPassword(pw);
            assertTrue(err.isEmpty());
        }
    }
}





