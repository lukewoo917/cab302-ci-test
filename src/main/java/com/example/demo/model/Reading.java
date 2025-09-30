package com.example.demo.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Reading object for a reading.
 * @param title The title of the reading
 * @param passage The passage of the reading or main paragraph
 * @param questions A list of question objects for the reading
 */
public record Reading(Integer id, String title, String passage, List<Question> questions, int difficulty) {

    // Constructor that includes ID (used when retrieving from database)
    public Reading(Integer id, String title, String passage, List<Question> questions, int difficulty) {
        this.id = id;
        this.title = title;
        this.passage = passage;
        this.questions = questions != null ? new ArrayList<>(questions) : new ArrayList<>();
        this.difficulty = difficulty;
    }

    @Override
    public List<Question> questions() {
        return Collections.unmodifiableList(questions);
    }

    public void addQuestion(Question q) {
        questions.add(q);
    }
}
