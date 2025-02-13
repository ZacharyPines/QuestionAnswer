import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        // Create Database and Scanner
        DatabaseHelper dbHelper = new DatabaseHelper();
        Scanner scanner = new Scanner(System.in);

        boolean run = true;

        try {
            dbHelper.connectToDatabase();
        } catch (Exception e) {
            System.out.println("Couldn't connect to database");
            run = false;
        }

        while (run) {

            System.out.println("Press 1 to ask a question. Press 2 to see all questions. Press 0 to exit.");
            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {

                try{
                    System.out.println("Enter your question:");
                    String questionText = scanner.nextLine();
                    dbHelper.addQuestion(questionText);
                    System.out.println("Your question has been added");
                } catch (Exception e) {
                    System.out.println("Couldn't add question");
                }

            } else if (choice == 2) {

                List<Question> questions = dbHelper.getAllQuestions();
                if (questions.size() == 0) {
                    System.out.println("No questions");
                    continue;
                }

                for (int i = 0; i < questions.size(); i++) {
                    Question q = questions.get(i);
                    int answerCount = dbHelper.getAnswerCount(q.getId());
                    System.out.printf("%d. %s [%d]\n", (i + 1), q.getText(), answerCount);
                }

                System.out.println("Press the number of a question to view. Press 0 to go back:");
                int questionChoice = scanner.nextInt();
                scanner.nextLine();

                if (questionChoice == 0) {
                    continue;
                }

                if (questionChoice < 1 || questions.size() < questionChoice) {
                    System.out.println("Invalid choice");
                    continue;
                }

                Question selectedQuestion = questions.get(questionChoice - 1);
                int selectedQuestionId = selectedQuestion.getId();

                while (true) {
                    System.out.println("Press 1 to view answers. Press 2 to answer this question. Press 0 to go back");
                    choice = scanner.nextInt();
                    scanner.nextLine();

                    if (choice == 1) {
                        List<Answer> answers = dbHelper.getAnswersForQuestion(selectedQuestionId);
                        if (answers.isEmpty()) {
                            System.out.println("No answers");
                        } else {
                            for (int i = 0; i < answers.size(); i++) {
                                Answer a = answers.get(i);
                                System.out.println("- " + a.getText());
                            }

                        }

                    } else if (choice == 2) {

                        try {
                            System.out.println("Enter your answer:");
                            String answerText = scanner.nextLine();
                            dbHelper.addAnswer(selectedQuestionId, answerText);
                            System.out.println("Your answer has been added.");
                        } catch (Exception e) {
                            System.out.println("Couldn't add answer");
                        }

                    } else if (choice == 0) {
                        break;
                    }

                    else {
                        System.out.println("Invalid choice");
                    }
                }

            } else if (choice == 0) {
                run = false;
                System.out.println("Thank you!");

            } else {
                System.out.println("Invalid choice");
            }
        }

        dbHelper.closeConnection();
        scanner.close();

    }
}
