import java.sql.*;

class Answer {
    private int id;
    private int questionId;
    private String text;
    private Timestamp createdAt;

    public Answer(int id, int questionId, String text, Timestamp createdAt) {
        this.id = id;
        this.questionId = questionId;
        this.text = text;
        this.createdAt = createdAt;
    }

    public String getText() {
        return text;
    }

}
