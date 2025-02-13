import java.sql.*;

public class Question {
    private int id;
    private String text;
    private Timestamp createdAt;

    public Question(int id, String text, Timestamp createdAt) {
        this.id = id;
        this.text = text;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

}

