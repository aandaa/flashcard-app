package flashcard;

import java.util.HashMap;
import java.util.Map;

public class AchievementTracker {
    private final Map<Card, Integer> totalAttempts = new HashMap<>();
    private final Map<Card, Integer> correctAttempts = new HashMap<>();

    public void track(Card card, int attempts, int correct) {
        totalAttempts.put(card, attempts);
        correctAttempts.put(card, correct);
    }

    public void evaluateAchievements(long durationMillis, int cardCount) {
        boolean allCorrect = correctAttempts.values().stream().allMatch(count -> count > 0);
        boolean repeatedCard = totalAttempts.values().stream().anyMatch(count -> count > 5);
        boolean confidentCard = correctAttempts.values().stream().anyMatch(count -> count >= 3);

        double avgSeconds = (durationMillis / 1000.0) / cardCount;

        System.out.println("\nAchievements:");
        if (avgSeconds < 5) {
            System.out.println("- FAST: Average under 5 seconds! 🚀");
        }
        if (allCorrect) {
            System.out.println("- CORRECT: All cards answered correctly! 🎯");
        }
        if (repeatedCard) {
            System.out.println("- REPEAT: More than 5 tries on a card! 🔁");
        }
        if (confidentCard) {
            System.out.println("- CONFIDENT: 3+ correct on a card! 💪");
        }
    }
}
