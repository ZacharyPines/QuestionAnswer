import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;

/**
 * The DatabaseHelper class is responsible for managing the connection to the database,
 * performing operations such as user registration, login validation, role management, and handling invitation codes.
 */

public class DatabaseHelper {

    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "org.h2.Driver";
    static final String DB_URL = "jdbc:h2:~/FoundationDatabase";

    // Database credentials
    static final String USER = "sa";
    static final String PASS = "";

    private Connection connection = null;
    private Statement statement = null;

    public void connectToDatabase() throws SQLException {
        try {
            Class.forName(JDBC_DRIVER); // Load the JDBC driver
            System.out.println("Connecting to database...");
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            statement = connection.createStatement();

            //You can use this command to clear the database and restart from fresh.
//			statement.execute("DROP ALL OBJECTS");

            createTables(); // Create tables if they don't exist
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found: " + e.getMessage());
        }
    }

    private void createTables() throws SQLException {
        String questionsTable = "CREATE TABLE IF NOT EXISTS Questions (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "questionText VARCHAR(500), " +
                "createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
        statement.execute(questionsTable);

        String answersTable = "CREATE TABLE IF NOT EXISTS Answers (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "questionId INT, " +
                "answerText VARCHAR(500), " +
                "createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (questionId) REFERENCES Questions(id) ON DELETE CASCADE)";
        statement.execute(answersTable);
    }

    public void addQuestion(String questionText) throws SQLException {
        String query = "INSERT INTO Questions (questionText) VALUES (?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, questionText);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error adding question");
        }
    }

    public void addAnswer(int questionId, String answerText) throws SQLException {
        String query = "INSERT INTO Answers (questionId, answerText) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, questionId);
            pstmt.setString(2, answerText);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error adding answer");
        }
    }

    public List<Question> getAllQuestions() {
        List<Question> questions = new ArrayList<>();
        String query = "SELECT id, questionText, createdAt FROM Questions";
        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String text = rs.getString("questionText");
                Timestamp createdAt = rs.getTimestamp("createdAt");
                questions.add(new Question(id, text, createdAt));
            }
        } catch (SQLException e) {
            System.out.println("Error getting questions");
        }
        return questions;
    }

    public List<Answer> getAnswersForQuestion(int questionId) {
        List<Answer> answers = new ArrayList<>();
        String query = "SELECT id, answerText, createdAt FROM Answers WHERE questionId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, questionId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String text = rs.getString("answerText");
                Timestamp createdAt = rs.getTimestamp("createdAt");
                answers.add(new Answer(id, questionId, text, createdAt));
            }
        } catch (SQLException e) {
            System.out.println("Error getting answers");
        }
        return answers;
    }

    public int getAnswerCount(int questionId) {
        String query = "SELECT COUNT(*) AS count FROM Answers WHERE questionId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, questionId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.out.println("Error getting answer count");
        }
        return 0;
    }

    public void closeConnection() {
        try{
            if(statement != null) statement.close();
        } catch(SQLException e) {
            System.out.println("Error closing connection");;
        }
    }
}
