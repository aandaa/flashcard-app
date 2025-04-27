package flashcard;

public class Card {
    private String question;
    private String answer;
    private int mistakeCount = 0;

    public Card(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public void invert() {
        String temp = this.question;
        this.question = this.answer;
        this.answer = temp;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public int getMistakeCount() {
        return mistakeCount;
    }

    public void markMistake() {
        mistakeCount++;
    }
}
