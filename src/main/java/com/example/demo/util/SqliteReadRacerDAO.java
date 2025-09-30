package com.example.demo.util;

import com.example.demo.exceptions.InvalidCredentialsException;
import com.example.demo.model.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.*;
import java.util.*;

/**
 * ✓ Methods TODO:
 * ✓ addUserAttempt (add an attempt on a reading to the database)
 * getAllUserAttemptsByReading (fetch a list of all user attempts of all users given a readingID)
 */

public class SqliteReadRacerDAO {
    private Connection connection;

    /**
     * Initialises the DAO by establishing a connection and creating all tables if they do not currently exist.
     * Uncomment insertSampleData() to insert Readings if they do not exist.
     */
    public SqliteReadRacerDAO() {
        connection = SqliteConnection.getInstance();

        //todo: Remove when database is static
        createTables();
        clearAllData();
        insertSampleData();
        insertSampleUsers();

    }

    private void createTables() {
        createReadingsTable();
        createQuestionsTable();
        createChoicesTable();
        createUsersTable();
        createUserAttemptsTable();
    }

    private void createReadingsTable() {
        try (Statement statement = connection.createStatement()) {
            String query = "CREATE TABLE IF NOT EXISTS Readings ("
                    + "reading_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "title VARCHAR NOT NULL,"
                    + "content VARCHAR NOT NULL,"
                    + "difficulty INT NOT NULL"
                    + ")";
            statement.execute(query);
//            System.out.println("Readings table created successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createUserAttemptsTable() {
        try (Statement statement = connection.createStatement()) {
            String query = "CREATE TABLE IF NOT EXISTS UserAttempts ("
                    + "attempt_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "user_id INT NOT NULL,"
                    + "reading_id INT NOT NULL,"
                    + "score INT NOT NULL,"
                    + "FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE,"
                    + "FOREIGN KEY (reading_id) REFERENCES Readings(reading_id) ON DELETE CASCADE"
                    + ")";
            statement.execute(query);
//            System.out.println("UserAttempts table created successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createUsersTable() {
        try (Statement statement = connection.createStatement()) {
            String query = "CREATE TABLE IF NOT EXISTS Users ("
                    + "user_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "username VARCHAR NOT NULL,"
                    + "email VARCHAR NOT NULL,"
                    + "password BLOB NOT NULL,"
                    + "salt BLOB NOT NULL"
                    + ")";
            statement.execute(query);
//            System.out.println("Users table created successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createChoicesTable() {
        try (Statement statement = connection.createStatement()) {
            String query = "CREATE TABLE IF NOT EXISTS Choices ("
                    + "choice_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "question_id INT NOT NULL,"
                    + "choice_text VARCHAR NOT NULL,"
                    + "is_correct BOOLEAN,"
                    + "FOREIGN KEY (question_id) REFERENCES Questions(question_id) ON DELETE CASCADE"
                    + ")";
            statement.execute(query);
//            System.out.println("Choices table created successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createQuestionsTable() {
        try (Statement statement = connection.createStatement()) {
            String query = "CREATE TABLE IF NOT EXISTS Questions ("
                    + "question_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "reading_id INT NOT NULL,"
                    + "prompt VARCHAR NOT NULL,"
                    + "FOREIGN KEY (reading_id) REFERENCES Readings(reading_id) ON DELETE CASCADE"
                    + ")";
            statement.execute(query);
            System.out.println("Questions table created successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * BE CAREFUL.
     * Clears all data from all database tables and resets auto-increment counters.
     * Used primarily for testing and development purposes.
     */
    public void clearAllData() {
        try (Statement statement = connection.createStatement()) {

            // Disable foreign key constraints temporarily
            statement.execute("PRAGMA foreign_keys = OFF");

            statement.execute("DELETE FROM UserAttempts");
            statement.execute("DELETE FROM Choices");
            statement.execute("DELETE FROM Questions");
            statement.execute("DELETE FROM Readings");
            statement.execute("DELETE FROM Users");

            // Reset auto-increment counters
            statement.execute("DELETE FROM sqlite_sequence WHERE name='UserAttempts'");
            statement.execute("DELETE FROM sqlite_sequence WHERE name='Choices'");
            statement.execute("DELETE FROM sqlite_sequence WHERE name='Questions'");
            statement.execute("DELETE FROM sqlite_sequence WHERE name='Readings'");
            statement.execute("DELETE FROM sqlite_sequence WHERE name='Users'");

            // Re-enable foreign key constraints
            statement.execute("PRAGMA foreign_keys = ON");
            System.out.println("All data cleared from database successfully");

        } catch (Exception e) {
            System.err.println("Error clearing database data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void clearAllTables() {
        try (Statement statement = connection.createStatement()) {

            // Disable foreign key constraints temporarily
            statement.execute("PRAGMA foreign_keys = OFF");

            statement.execute("DROP TABLE  IF EXISTS UserAttempts");
            statement.execute("DROP TABLE  IF EXISTS Choices");
            statement.execute("DROP TABLE  IF EXISTS Questions");
            statement.execute("DROP TABLE  IF EXISTS Readings");
            statement.execute("DROP TABLE  IF EXISTS Users");

            // Reset auto-increment counters
            statement.execute("DROP TABLE IF EXISTS sqlite_sequence");


            // Re-enable foreign key constraints
            statement.execute("PRAGMA foreign_keys = ON");
            System.out.println("All data cleared from database successfully");

        } catch (Exception e) {
            System.err.println("Error clearing database data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Clears all data and inserts base sample readings.
     */
    private void insertSampleData() {
        try {
            insertSampleReadings();

            System.out.println("Sample data inserted successfully");

        } catch (Exception e) {
            System.err.println("Error inserting test data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void insertSampleUsers() {
        //copy paste from registration okay for testing but need a more universal way of adding users programmatically
        // todo clean up this jank
        // Collect all errors

        String username = "dev";
        String email = "dev";
        String password = "dev";

        // password hashing with md5, Not recommended for production but good enough for now.
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        md.update(salt);
        byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));

        // todo: confirmation screen?

        User user = new User(username, email, hashedPassword, salt);

        try {
            addUser(user);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Inserts predefined sample readings into the database.
     * Creates a list of sample Reading objects with associated questions and inserts them
     * along with their questions and respective choices into the database.
     *
     * @throws SQLException if there's an error during database insertion
     */
    private void insertSampleReadings() throws SQLException {
        List<Reading> readings = List.of(
                new Reading(
                        null,
                        "The Fox and the Grapes",
                        "A hungry fox saw some fine bunches of grapes hanging from a vine. "
                                + "He did his best to reach them, but they were just out of reach. "
                                + "Finally, he gave up and said, 'They're probably sour anyway.'",
                        List.of(
                                new Question("What fruit did the fox want?", List.of(
                                        new Choice("Apples", false),
                                        new Choice("Grapes", true),
                                        new Choice("Pears", false)
                                )),
                                new Question("Why did the fox give up?", List.of(
                                        new Choice("Grapes Too High", true),
                                        new Choice("Apples Too High", false),
                                        new Choice("Too low", false)
                                ))
                        ),
                        1
                ),
                new Reading(
                        null,
                        "The Tortoise and the Hare",
                        "A hare mocked a slow-moving tortoise. The tortoise challenged the hare to a race. "
                                + "The hare ran far ahead, then took a nap, confident of winning. "
                                + "The tortoise kept going slowly and steadily, eventually winning the race.",
                        List.of(
                                new Question("Who won the race?", List.of(
                                        new Choice("Hare", false),
                                        new Choice("Tortoise", true)
                                )),
                                new Question("What is the moral of the story?", List.of(
                                        new Choice("Slow and Steady wins", true),
                                        new Choice("Fast and crazy Wins", false),
                                        new Choice("Boring story", false)
                                ))
                        ),
                        1
                ),
                new Reading(
                        null,
                        "The Lion and the Mouse",
                        "A lion caught a small mouse. The mouse begged for its life, promising to help someday. "
                                + "The lion laughed but let it go. Later, hunters trapped the lion in a net. "
                                + "The mouse chewed through the ropes and freed the lion.",
                        List.of(
                                new Question("Who helped the lion escape?", List.of(
                                        new Choice("The hunters", false),
                                        new Choice("The mouse", true),
                                        new Choice("Another lion", false)
                                )),
                                new Question("What lesson does this story teach?", List.of(
                                        new Choice("Being useful", true),
                                        new Choice("Being reliable", false),
                                        new Choice("Being angry", false)
                                ))
                        ),
                        1
                ),
                new Reading(
                        null,
                        "The Boy Who Cried Wolf",
                        "A shepherd boy liked to play tricks. He repeatedly cried 'Wolf!' when there was none, "
                                + "and the villagers rushed to help. Later, when a wolf truly appeared, no one believed him, "
                                + "and his sheep were eaten.",
                        List.of(
                                new Question("What did the boy lie about?", List.of(
                                        new Choice("Seeing a wolf", true),
                                        new Choice("Losing sheep", false),
                                        new Choice("Falling ill", false)
                                )),
                                new Question("Why didn't the villagers help him at the end?", List.of(
                                        new Choice("They doubted", true),
                                        new Choice("They believed", false),
                                        new Choice("They hated", false)
                                ))
                        ),
                        1
                ),
                new Reading(
                        null,
                        "The Ant and the Grasshopper",
                        "All summer long, the ant worked hard gathering food, while the grasshopper sang and played. "
                                + "When winter came, the grasshopper had nothing to eat, while the ant lived comfortably "
                                + "on its stored supplies.",
                        List.of(
                                new Question("What did the ant do during summer?", List.of(
                                        new Choice("Played music", false),
                                        new Choice("Gathered food", true),
                                        new Choice("Slept", false)
                                )),
                                new Question("What happened to the grasshopper in winter?", List.of(
                                        new Choice("No food", true),
                                        new Choice("Too much food", false),
                                        new Choice("Sleeping", false)
                                ))
                        ),
                        1
                )
        );

        for (Reading reading : readings) {
            Reading insertedReading = addReading(reading);
            for (Question question : reading.questions()) {
                addQuestion(insertedReading.id(), question);
            }
        }
    }

    /**
     * Retrieves a specific reading from the database by its ID.
     * Includes all associated questions and their choices.
     *
     * @param id The unique identifier of the reading to retrieve
     * @return Reading object if found, null if no reading exists with the given ID
     */
    public Reading getReading(int id) {
        final String sql = "SELECT reading_id, title, content, difficulty FROM readings WHERE reading_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                final String title = rs.getString("title");
                final String passage = rs.getString("content");
                final int difficulty = rs.getInt("difficulty");
                final List<Question> questions = getQuestions(id);
                return new Reading(id, title, passage, questions, difficulty);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve reading with id=" + id, e);
        }
    }

    /**
     * Retrieves a random reading from the database.
     * Useful for providing users with varied reading content.
     *
     * @return A randomly selected Reading object
     * @throws RuntimeException if database operation fails or no readings exist
     */
    public Reading getRandomReading() {
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("SELECT reading_id FROM readings ORDER BY RANDOM() LIMIT 1")) {

            if (rs.next()) {
                int id = rs.getInt("reading_id");
                return getReading(id);
            }
            throw new RuntimeException("No readings found in database");

        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve random reading", e);
        }
    }
    /**
     * Adds a new reading to the database and returns the reading with its generated ID.
     * This method only inserts the reading itself; questions must be added separately.
     *
     * @param reading The Reading object to add to the database
     * @return Reading object with the generated database ID
     * @throws SQLException if there's an error during database insertion or ID retrieval
     */
    public Reading addReading(Reading reading) throws SQLException {
        try (PreparedStatement readingStmt = connection.prepareStatement(
                "INSERT INTO Readings (title, content, difficulty) VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) { // Add this flag to get generated ID

            readingStmt.setString(1, reading.title());
            readingStmt.setString(2, reading.passage());
            readingStmt.setInt(3, reading.difficulty());

            int rowsAffected = readingStmt.executeUpdate(); // Use executeUpdate instead of execute

            if (rowsAffected > 0) {
                // Get the generated ID
                try (ResultSet generatedKeys = readingStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);

                        // Create a new Reading instance with the ID
                        Reading readingWithId = new Reading(generatedId, reading.title(),
                                reading.passage(), reading.questions(), reading.difficulty());

                        return readingWithId;
                    }
                }
            }
        }

        throw new SQLException("Failed to insert reading or retrieve generated ID");
    }

    /**
     * Adds a new question with its choices to a specific reading in the database.
     * Creates entries in both Questions and Choices tables, maintaining referential integrity.
     *
     * @param readingId The ID of the reading to which this question belongs
     * @param question The Question object containing the prompt, choices, and correct answer
     * @throws SQLException if there's an error during database insertion
     */
    public void addQuestion(int readingId, Question question) throws SQLException {
        try (PreparedStatement questionStmt = connection.prepareStatement(
                "INSERT INTO Questions (reading_id, prompt) VALUES (?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {

            questionStmt.setInt(1, readingId);
            questionStmt.setString(2, question.prompt());

            int affected = questionStmt.executeUpdate();
            if (affected != 1) {
                throw new SQLException("Inserting question failed, affected rows = " + affected);
            }

            int questionId;
            try (ResultSet generatedKeys = questionStmt.getGeneratedKeys()) {
                if (!generatedKeys.next()) {
                    throw new SQLException("Inserting question failed, no generated key returned.");
                }
                questionId = generatedKeys.getInt(1);
            }

            try (PreparedStatement choiceStmt = connection.prepareStatement(
                    "INSERT INTO Choices (question_id, choice_text, is_correct) VALUES (?, ?, ?)")) {

                for (Choice choice : question.choices()) {
                    choiceStmt.setInt(1, questionId);
                    choiceStmt.setString(2, choice.getChoiceText());
                    choiceStmt.setBoolean(3, choice.isCorrect());
                    choiceStmt.addBatch(); // faster than per-row executeUpdate
                }
                choiceStmt.executeBatch();
            }
        }
    }

    public boolean userWithIdExists(int userId) throws SQLException {
        Objects.requireNonNull(userId, "userId");
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT COUNT(*) FROM Users WHERE user_id = ?")) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count == 1;
                }
                return false;
            }
                }
    }

    public List<Question> getQuestions(int reading_id) {
        List<Question> questions = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT question_id, prompt FROM Questions WHERE reading_id = ?")) {
            statement.setInt(1, reading_id);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {

                    int question_id = resultSet.getInt("question_id");
                    String prompt = resultSet.getString("prompt");
                    //choices retrieved from the choices table
                    List<Choice> choices = getChoices(question_id);

                    // Create a Question record using the appropriate constructor
                    Question question = new Question(prompt, choices);

                    System.out.println(question);

                    questions.add(question);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve questions for reading_id=" + reading_id, e);
        }
        return questions;
    }

    public List<Choice> getChoices(int question_id) throws SQLException {
        List<Choice> choices = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT choice_id,choice_text,is_correct FROM Choices WHERE question_id = ? ORDER BY choice_id")) {
            statement.setInt(1, question_id);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int choice_id = resultSet.getInt("choice_id");
                    boolean is_correct = resultSet.getBoolean("is_correct");
                    String choice_text = resultSet.getString("choice_text");
                    choices.add(new Choice(choice_id, choice_text, is_correct));
                }
            }
        }
        return choices;
    }

    public User addUser(User user) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO Users (username, email, password, salt) VALUES (?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, user.getUsername());
            statement.setString(2, user.getEmail());
            statement.setBytes(3, user.getPassword());
            statement.setBytes(4, user.getSalt());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        user.setUser_id(generatedId);
                        return user;
                    }
                }
            }
        }

        throw new SQLException("Failed to add user to database");
    }

    public User getUser(int userId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT username, email, password, salt FROM Users WHERE user_id = ?")) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    String username = resultSet.getString("username");
                    String email = resultSet.getString("email");
                    byte[] password = resultSet.getBytes("password");
                    byte[] salt = resultSet.getBytes("salt");

                    return new User(username, email, password, salt);
                }
            }
        }
        throw new SQLException("User not found with ID: " + userId);
    }


    public boolean usernameIsUnique(String login) throws SQLException {
        Objects.requireNonNull(login, "login");

        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT COUNT(*) FROM Users WHERE username = ?")) {
            ps.setString(1, login);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count == 0;
                }
                return true;
            }
        }
    }

    public boolean emailIsUnique(String login) throws SQLException {
        Objects.requireNonNull(login, "login");

        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT COUNT(*) FROM Users WHERE email = ?")) {
            ps.setString(1, login);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count == 0;
                }
                return true;
            }
        }
    }

    public void updateUser(User newuser) throws SQLException {
        Objects.requireNonNull(newuser, "newuser");
        if (!newuser.has_id()) {
            throw new SQLException("User to update has no ID");
        }
        //todo find better way of doing this
//        if (!usernameIsUnique(newuser.getUsername()) ) {
//            throw new SQLException("Username is not unique");
//        }
//        if (!emailIsUnique(newuser.getEmail())) {
//            throw new SQLException("Email is not unique");
//        }
        if (!userWithIdExists(newuser.getUser_id())) {
            throw new SQLException("User with ID does not exist");
        }

        try (PreparedStatement ps = connection.prepareStatement(
                "UPDATE Users SET username = ?, email = ?, password = ?, salt = ? WHERE user_id = ?")) {
            ps.setString(1, newuser.getUsername());
            ps.setString(2, newuser.getEmail());
            ps.setBytes(3, newuser.getPassword());
            ps.setBytes(4, newuser.getSalt());
            ps.setInt(5, newuser.getUser_id());

            ps.executeUpdate();
                }
        catch (SQLException e) {
            throw new SQLException("Failed to update user", e);
        }


    }

    public void deleteUser(int userId) throws SQLException {
        if (!userWithIdExists(userId)) {
            throw new SQLException("User with ID does not exist");
        }
        try (PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM Users WHERE user_id = ?")) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }

    public User TryLogin(String login, String rawPassword) throws SQLException {

        Objects.requireNonNull(login, "login");
        Objects.requireNonNull(rawPassword, "password");

        // can be email or username login
        final String sql = """
        SELECT user_id, username, email, salt, password
        FROM users
        WHERE username = ? OR email = ?
        LIMIT 1
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, login);
            ps.setString(2, login);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new InvalidCredentialsException(); // user not found
                }

                int id = rs.getInt("user_id");
                String username = Objects.requireNonNull(rs.getString("username"));
                String email    = Objects.requireNonNull(rs.getString("email"));
                byte[] salt     = Objects.requireNonNull(rs.getBytes("salt"));
                byte[] stored   = Objects.requireNonNull(rs.getBytes("password"));

                byte[] computed;
                try {
                    MessageDigest md = MessageDigest.getInstance("SHA-512");
                    md.update(salt);
                    computed = md.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
                } catch (NoSuchAlgorithmException e) {
                    throw new IllegalStateException("SHA-512 not available", e);
                }

                if (!java.util.Arrays.equals(stored, computed)) {
                    throw new InvalidCredentialsException(); // wrong password
                }

                return new User(id, username, email, stored, salt);
            }
        }
    }

    /**
     * Adds a UserAttempt to the database, tied to reading_id and user_id.
     *
     * @param userAttempt the UserAttempt object to be added to the database.
     * @return UserAttempt object with database generated ID.
     */

    public void addUserAttempt(UserAttempt userAttempt) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO UserAttempts (user_id, reading_id, score) VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, userAttempt.getUserID());
            statement.setInt(2, userAttempt.getReadingID());
            statement.setInt(3, userAttempt.getScore());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        userAttempt.setAttemptID(generatedId);
                        return;
                    }
                }
            }
        }

        throw new SQLException("Failed to add user attempt to database");
    }

    /**
     * Retrieves a leaderboard for the given reading
     *
     * @param readingId the ID of the reading for which to produce the leaderboard for.
     * @return A list of UserAttempt objects sorted by score.
     */

    public List<UserAttempt> getLeaderboard (int readingId) throws SQLException {
        List<UserAttempt> leaderboard = new ArrayList<>();

        final String sql = """
            SELECT ua.attempt_id, ua.user_id, ua.reading_id, ua.score
                    FROM UserAttempts ua
                    INNER JOIN (
                        SELECT user_id, MAX(score) as max_score
                        FROM UserAttempts
                        WHERE reading_id = ?
                        GROUP BY user_id
                    ) best ON ua.user_id = best.user_id AND ua.score = best.max_score AND ua.reading_id = ?
                    ORDER BY ua.score DESC
                    LIMIT 10
            """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, readingId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int attempt_id = resultSet.getInt("attempt_id");
                    int user_id = resultSet.getInt("user_id");
                    int reading_id = resultSet.getInt("reading_id");
                    int score = resultSet.getInt("score");

                    UserAttempt attempt = new UserAttempt(attempt_id, user_id, reading_id, score);
                    leaderboard.add(attempt);
                }
            }
        }
        return leaderboard;
    }

}