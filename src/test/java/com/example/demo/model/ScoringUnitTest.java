package com.example.demo.model;

import com.example.demo.exceptions.ScoringException;
import com.example.demo.util.CountdownTimer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
//test
class ScoringUnitTest {
    Reading testReading = new Reading(
            null,
            "The Boy Who Cried Wolf",
            "A shepherd boy liked to play tricks. He repeatedly cried 'Wolf!' when there was none, "
                    + "and the villagers rushed to help. Later, when a wolf truly appeared, no one believed him, "
                    + "and his sheep were eaten.",
            List.of(
                    new Question("What did the boy lie about?", List.of(
                            new Choice("Seeing a wolf", true),
                            new Choice("Losing sheep", false),
                            new Choice("Falling ill", false))),
                    new Question("Why didn't the villagers help him at the end?", List.of(
                            new Choice("They doubted", true),
                            new Choice("They believed", false),
                            new Choice("They hated", false)))
            ),
            1
    );

    //This is a list of intentionally correct answers
    List<Choice> allCorrectUserAnswer= List.of(new Choice("Seeing a wolf", true), new Choice("They doubted", true));

    //This is a list of intentionally incorrect answers
    List<Choice> allIncorrectUserAnswer= List.of(new Choice("Losing sheep", false),
            new Choice("Falling ill", false));

    //This is a list of mismatched number of answers.
    List<Choice> mismatchedUserAnswer= List.of(new Choice("Seeing a wolf", true));

    @Test void nullInputs_throwIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> ScoringSystem.score(null, null,null));
    }

    @Test void mismatchedAnswerCount_throwsScoringException() {
        int timeRemaining=0;
        assertThrows(ScoringException.class, () -> ScoringSystem.score(mismatchedUserAnswer, testReading.questions(),timeRemaining));
    }

    @Test void allIncorrect_noTimeBonus_scoresZero() throws Exception {
        //Time remaining=0
        int timeRemaining=0;
        //Score
        QuizResult results= ScoringSystem.score(allIncorrectUserAnswer,testReading.questions(),timeRemaining);

        //Expect 0 (no correct answers, no time bonus)
        assertEquals(0, results.score());
    }

    @Test void allCorrect_noTimeBonus_scoresTwenty(){

        //Time remaining=0
        int timeRemaining=0;

        //Score
        QuizResult results= ScoringSystem.score(allCorrectUserAnswer,testReading.questions(),timeRemaining);

        //Expect 20 (2 * 10 points), no time bonus
        assertEquals(20, results.score());
    }


    @Test
    void noCorrect_withTimeBonus_scoresBonusOnly(){
        int timeRemaining = 100;

        QuizResult r = ScoringSystem.score(allIncorrectUserAnswer,testReading.questions(),timeRemaining);

        assertEquals(10,r.score()); //expect 100s time remaining = 10 points
    }

    @Test
    void all_correct_withTimeBonus_addsBasePlusBonus(){
        int timeRemaining=100;

        QuizResult r = ScoringSystem.score(allCorrectUserAnswer,testReading.questions(),timeRemaining);

        assertEquals(30,r.score()); // 20 base + 10 bonus
    }

    @Test
    void nullSeconds_countsAsZeroBonus(){
        Integer timeRemaining=null;

        QuizResult r = ScoringSystem.score(allCorrectUserAnswer,testReading.questions(),timeRemaining);

        assertEquals(20,r.score()); //no time bonus when null;
    }

    @Test
    void negativeSeconds_countsAsZeroBonus(){
        int timeRemaining=-50;

        QuizResult r = ScoringSystem.score(allCorrectUserAnswer,testReading.questions(),timeRemaining);

        assertEquals(20,r.score()); //no time bonus when null;
    }

    @Test
    void result_echoesInputs_totalAndOrder(){
        int timeRemaining=10;

        QuizResult r = ScoringSystem.score(allCorrectUserAnswer,testReading.questions(),timeRemaining);

        assertEquals(21,r.score()); //20 + 1 bonus
        assertEquals(2,r.totalQuestions()); //2 questions total
        assertEquals(2,r.questionsAnswered());//2 questions answered
        assertEquals(List.of("Correct","Correct"),r.perQuestionResult());
    }

    @Test
    void timeBonus_flooringBoundaries(){

        //9s remaining -> 0. 10s -> 1, 19s->1 and so on.

        assertEquals(0,ScoringSystem.score(allIncorrectUserAnswer,testReading.questions(),9).score());
        assertEquals(1,ScoringSystem.score(allIncorrectUserAnswer,testReading.questions(),10).score());
        assertEquals(1,ScoringSystem.score(allIncorrectUserAnswer,testReading.questions(),19).score());
        assertEquals(2,ScoringSystem.score(allIncorrectUserAnswer,testReading.questions(),20).score());
    }
}