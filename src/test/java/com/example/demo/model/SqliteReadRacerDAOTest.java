package com.example.demo.model;
import com.example.demo.util.SqliteConnection;
import com.example.demo.util.SqliteReadRacerDAO;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class SqliteReadRacerDAOTest {
    private SqliteReadRacerDAO dao;

    @BeforeEach
    void setUp() {
        dao = new SqliteReadRacerDAO();
        dao.clearAllData();
        insertTestData();
    }

    public void insertTestData() {
        try {
            Connection connection = SqliteConnection.getInstance();

            insertTestReadings(connection);
            insertTestQuestions(connection);
            insertTestChoices(connection);

            System.out.println("Test data inserted successfully");

        } catch (Exception e) {
            System.err.println("Error inserting test data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void insertTestChoices(Connection connection) throws Exception {
        PreparedStatement choiceStmt = connection.prepareStatement(
                "INSERT INTO Choices (question_id, choice_text, is_correct) VALUES (?, ?, ?)");

        // Choices for Question 1 (question_id = 1): "What is the main topic of Reading 1?"
        choiceStmt.setInt(1, 1);
        choiceStmt.setString(2, "Topic A");
        choiceStmt.setBoolean(3, false);
        choiceStmt.execute();

        choiceStmt.setInt(1, 1);
        choiceStmt.setString(2, "Topic B");
        choiceStmt.setBoolean(3, true);  // Correct answer
        choiceStmt.execute();

        choiceStmt.setInt(1, 1);
        choiceStmt.setString(2, "Topic C");
        choiceStmt.setBoolean(3, false);
        choiceStmt.execute();

        // Choices for Question 2 (question_id = 2): "What difficulty level is this reading?"
        choiceStmt.setInt(1, 2);
        choiceStmt.setString(2, "Easy");
        choiceStmt.setBoolean(3, true);  // Correct answer (matches difficulty = 1)
        choiceStmt.execute();

        choiceStmt.setInt(1, 2);
        choiceStmt.setString(2, "Medium");
        choiceStmt.setBoolean(3, false);
        choiceStmt.execute();

        choiceStmt.setInt(1, 2);
        choiceStmt.setString(2, "Hard");
        choiceStmt.setBoolean(3, false);
        choiceStmt.execute();

        // Choices for Question 3 (question_id = 3): "What is the content about in Reading 2?"
        choiceStmt.setInt(1, 3);
        choiceStmt.setString(2, "Science");
        choiceStmt.setBoolean(3, false);
        choiceStmt.execute();

        choiceStmt.setInt(1, 3);
        choiceStmt.setString(2, "General Content");
        choiceStmt.setBoolean(3, true);  // Correct answer
        choiceStmt.execute();

        choiceStmt.setInt(1, 3);
        choiceStmt.setString(2, "Mathematics");
        choiceStmt.setBoolean(3, false);
        choiceStmt.execute();

        choiceStmt.close();
        System.out.println("Test choices inserted");
    }

    private void insertTestQuestions(Connection connection) throws Exception{
        PreparedStatement questionStmt = connection.prepareStatement(
                "INSERT INTO Questions (reading_id, prompt) VALUES (?, ?)");

        // Questions for Reading 1 (reading_id = 1)
        questionStmt.setInt(1, 1);
        questionStmt.setString(2, "What is the main topic of Reading 1?");
        questionStmt.execute();

        questionStmt.setInt(1, 1);
        questionStmt.setString(2, "What difficulty level is this reading?");
        questionStmt.execute();

        // Questions for Reading 2 (reading_id = 2)
        questionStmt.setInt(1, 2);
        questionStmt.setString(2, "What is the content about in Reading 2?");
        questionStmt.execute();

        questionStmt.close();
        System.out.println("Test questions inserted");
    }

    private void insertTestReadings(Connection connection) {
        try {
            PreparedStatement clearStmt = connection.prepareStatement("DELETE FROM Readings");
            clearStmt.execute();

            PreparedStatement readingStmt = connection.prepareStatement(
                    "INSERT INTO Readings (title, content, difficulty) VALUES (?, ?, ?)");

            readingStmt.setString(1, "Reading 1");
            readingStmt.setString(2, "Content 1");
            readingStmt.setInt(3, 1);
            readingStmt.execute();

            readingStmt.setString(1, "Reading 2");
            readingStmt.setString(2, "Content 2");
            readingStmt.setInt(3, 2);
            readingStmt.execute();

            System.out.println("Test readings inserted");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @AfterEach
    void tearDown() {
        // Clean up test data if needed, add a method:
         //dao.clearAllData();
    }

    @Test
    void testGetReading() {
        // Test with existing sample data (ID 1 should exist from insertReadingSample)
        Reading reading = dao.getReading(1);

        assertNotNull(reading, "Reading should not be null");
        assertEquals("Reading 1", reading.title());
        assertEquals("Content 1", reading.passage());
        assertEquals(1, reading.difficulty());
    }

    @Test
    void testGetReadingNotFound() {
        // Test with non-existent ID
        Reading reading = dao.getReading(999);
        assertNull(reading, "Reading should be null for non-existent ID");
    }

    @Test
    void testGetChoices() {
        List<Choice> choices = null;
        try {
            choices = dao.getChoices(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        assertNotNull(choices, "Choices should not be null");
    }

    @Test
    void testGetQuestions() {
        // Test getting questions for reading_id = 1
        List<Question> questions = dao.getQuestions(1);

        assertNotNull(questions, "Questions list should not be null");
        // Add more assertions later
    }
}
