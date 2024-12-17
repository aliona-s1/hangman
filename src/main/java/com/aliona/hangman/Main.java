package com.aliona.hangman;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class Main {
    private static final Scanner SCANNER = new Scanner(System.in);
    private static final Random RANDOM = new Random();
    private static final List<String> WORDS = new ArrayList<>();
    private static final String START = "да";
    private static final String EXIT = "нет";
    private static final int MAX_MISTAKES = 6;
    private static final String MASKING_SYMBOL = "*";

    public static void main(String[] args) {
        startMenu();
    }

    private static void startMenu() {

        while (true) {
            System.out.println("\nХотите сыграть? да/нет");
            String letter = SCANNER.nextLine().trim().toLowerCase();

            if (letter.equals(START)) {
                initializeDictionary();
                startGame();
            } else if (letter.equals(EXIT)) {
                System.out.println("Вы вышли из игры.");
                SCANNER.close();
                break;
            } else {
                System.out.println("Введите да или нет.");
            }
        }
    }

    private static void initializeDictionary() {
        if (!WORDS.isEmpty()) {
            return;
        }

        try (InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("dictionary.txt");
             Scanner scanner = new Scanner(inputStream)) {

            while (scanner.hasNextLine()) {
                String word = scanner.nextLine().trim();

                if (!word.isEmpty()) {
                    WORDS.add(word);
                }
            }

            if (WORDS.isEmpty()) {
                throw new RuntimeException("Словарь пуст.");
            }
        } catch (NullPointerException | IOException e) {
            throw new RuntimeException("Файл не найден.");
        }
    }

    private static void startGame() {
        System.out.println("\nНачинаем игру!");

        String word = WORDS.get(RANDOM.nextInt(WORDS.size()));
        String maskedWord = MASKING_SYMBOL.repeat(word.length());

        startGameLoop(word, maskedWord);
    }

    private static void startGameLoop(String word, String maskedWord) {
        int mistakes = 0;
        List<String> inputtedLetters = new ArrayList<>();

        printHangman(0);

        while (!isGameFinished(maskedWord, mistakes, word)) {
            showMaskedWord(maskedWord);

            String letter = inputValidLetter(inputtedLetters);

            if (isLetterInWord(word, letter)) {
                maskedWord = updateMaskedWord(word, maskedWord, letter);
            } else {
                System.out.println("Такой буквы в слове нет!");
                mistakes++;
            }

            inputtedLetters.add(letter);

            showGameState(mistakes, inputtedLetters);
        }

        if (isWin(word, maskedWord)) {
            System.out.println("Поздравляем! Вы угадали слово: " + word);
            return;
        }

        if (isLoss(mistakes)) {
            System.out.println("Вы проиграли! Загаданное слово: " + word);
        }
    }

    private static void showMaskedWord(String maskedWord) {
        System.out.println("\n" + maskedWord);
    }

    private static String inputValidLetter(List<String> inputtedLetters) {
        while (true) {
            String letter = SCANNER.nextLine().toLowerCase();

            if (letter.length() != 1) {
                System.out.println("Введите одну букву.");
                continue;
            }

            if (!letter.matches("[а-яА-ЯЁё]")) {
                System.out.println("Введите букву русского алфавита.");
                continue;
            }

            if (inputtedLetters.contains(letter)) {
                System.out.println("Эта буква уже вводилась! Попробуйте другую.");
                continue;
            }
            return letter;
        }
    }

    private static boolean isLetterAbsent(String word, String letter) {
        return (!word.contains(letter));
    private static boolean isLetterInWord(String word, String letter) {
        return word.contains(letter);
    }

    private static String updateMaskedWord(String word, String maskedWord, String letter) {
        StringBuilder updatedMaskedWord = new StringBuilder(maskedWord);

        for (int i = 0; i < word.length(); i++) {
            String currentLetter = word.substring(i, i + 1);

            if (currentLetter.equals(letter)) {
                updatedMaskedWord.replace(i, i + 1, currentLetter);
            }
        }
        return updatedMaskedWord.toString();
    }

    private static void showGameState(int mistakes, List<String> inputtedLetters) {
        printHangman(mistakes);
        System.out.printf("Ошибок: %s из %s", mistakes, MAX_MISTAKES);
        System.out.println("\nВведенные буквы: " + String.join(",", inputtedLetters));
    }

    private static boolean isWin(String word, String maskedWord) {
        return maskedWord.equals(word);
    }

    private static boolean isLoss(int mistakes) {
        return mistakes == MAX_MISTAKES;
    }

    private static boolean isGameFinished(String maskedWord, int mistakes, String word) {
        return isLoss(mistakes) || isWin(word, maskedWord);
    }

    private static void printHangman(int mistakes) {
        String[] hangmanStages =
                {"""
   ______
   |    |
   |
   |
   |
   |
===========
""", """
   ______
   |    |
   |    0
   |
   |
   |
===========
""", """
   ______
   |    |
   |    0
   |    |
   |
   |
===========
""", """
   ______
   |    |
   |    0
   |   /|
   |
   |
===========
""", """
   ______
   |    |
   |    0
   |   /|\\
   |
   |
===========
""", """
   ______
   |    |
   |    0
   |   /|\\
   |   /
   |
===========
""", """
   ______
   |    |
   |    0
   |   /|\\
   |   / \\
   |
===========
"""};
        System.out.print(hangmanStages[mistakes]);
    }
}