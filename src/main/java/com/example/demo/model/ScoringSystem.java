package com.example.demo.model;

import com.example.demo.exceptions.ScoringException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ScoringSystem {

    private ScoringSystem(){}

    private static final int POINTS_PER_CORRECT=10;

    private static final int POINTS_PER_10S_REMAINING=1;



    public static QuizResult score(List<Choice> userAnswers, List<Question> questions, Integer secondsRemaining) {
        //Make it immutable (always use snapshot of values at construction time)
        if (userAnswers == null || userAnswers.isEmpty() || questions == null || questions.isEmpty())
        {
            throw new IllegalArgumentException("User answers and correct answers should not be empty or null");
        }
        if (userAnswers.size() != questions.size()) {
            throw new ScoringException("User answers size do not match with correct answers size");
        }

        int score=0;

        List<String> results=new ArrayList<>(questions.size());


        //Calculate the accuracy only score
        for(int i=0;i<questions.size();i++){

            Choice userAnswer= userAnswers.get(i);

            if(userAnswer==null){
                results.add("Missing answer");
                continue;
            }
            if(userAnswer.isCorrect()){
                score+=POINTS_PER_CORRECT;
                results.add("Correct");
            }else{
                results.add("Incorrect");
            }
        }

        //Time bonus

        int timeLeft=Math.max(0,secondsRemaining==null?0:secondsRemaining);
        score+=(int) ((timeLeft/10)*POINTS_PER_10S_REMAINING);

        int answered = (int) userAnswers.stream().filter(Objects::nonNull).count();


        return new QuizResult(
                score,
                questions.size(),
                answered,
                questions,
                userAnswers,
                results
        );
    }

}
