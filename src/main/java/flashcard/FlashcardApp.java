package flashcard;

import java.io.*;
import java.util.*;

public class FlashcardApp {
    public static void main(String[] args) {
        if (args.length == 0 || Arrays.asList(args).contains("--help")) {
            showHelp();
            return;
        }

        String filePath = args[0];
        Map<String, String> options = parseOptions(Arrays.copyOfRange(args, 1, args.length));

        List<Card> cards = loadCards(filePath);
        if (cards == null) return;

        if (options.containsKey("--invertCards")) {
            for (Card card : cards) {
                card.invert();
            }
        }

        CardOrganizer organizer = getOrganizer(options.getOrDefault("--order", "random"));
        AchievementTracker tracker = new AchievementTracker();

        int repetitions = Integer.parseInt(options.getOrDefault("--repetitions", "1"));
        Scanner scanner = new Scanner(System.in);

        List<Card> organizedCards = organizer.organize(cards);

        long startTime = System.currentTimeMillis();

        for (Card card : organizedCards) {
            int correctCount = 0;
            int totalAttempts = 0;
            while (correctCount < repetitions) {
                System.out.println("Question: " + card.getQuestion());
                String answer = scanner.nextLine();
                totalAttempts++;
                if (answer.equalsIgnoreCase(card.getAnswer())) {
                    System.out.println("Correct!");
                    correctCount++;
                } else {
                    System.out.println("Wrong! Correct answer: " + card.getAnswer());
                    card.markMistake();
                }
            }
            tracker.track(card, totalAttempts, correctCount);
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        tracker.evaluateAchievements(duration, organizedCards.size());

        scanner.close();
    }

    private static void showHelp() {
        System.out.println("Usage: flashcard <cards-file> [options]");
        System.out.println("Options:");
        System.out.println("--help Show help information");
        System.out.println("--order <order> Sorting type [random, worst-first, recent-mistakes-first]");
        System.out.println("--repetitions <num> Number of correct answers required per card");
        System.out.println("--invertCards Invert questions and answers");
    }

    private static Map<String, String> parseOptions(String[] args) {
        Map<String, String> options = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("--")) {
                if (i + 1 < args.length && !args[i + 1].startsWith("--")) {
                    options.put(args[i], args[i + 1]);
                    i++;
                } else {
                    options.put(args[i], "true");
                }
            }
        }
        return options;
    }

    private static List<Card> loadCards(String filePath) {
        List<Card> cards = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";", 2);
                if (parts.length == 2) {
                    cards.add(new Card(parts[0].trim(), parts[1].trim()));
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load cards file: " + e.getMessage());
            return null;
        }
        return cards;
    }

    private static CardOrganizer getOrganizer(String order) {
        return switch (order) {
            case "recent-mistakes-first" -> new RecentMistakesFirstSorter();
            default -> cards -> {
                Collections.shuffle(cards);
                return cards;
            };
        };
    }
}
