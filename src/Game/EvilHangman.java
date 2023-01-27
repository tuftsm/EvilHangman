package Game;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class EvilHangman {

    public static void main(String[] args) {

        File myDict = new File(args[0]);
        int wordLength = Integer.parseInt(args[1]);
        int guesses = Integer.parseInt(args[2]);
        int guessesRemaining = guesses;
        Set<String> currSubset = new TreeSet<>();
        if (wordLength < 2) {
            System.out.println("Word length must be at least 2. Please try again.");
        }
        if (guesses < 1) {
            System.out.println("Guesses must be at least 1. Please try again.");
        }
        EvilHangmanGame playerGame = new EvilHangmanGame();
        try {
            System.out.println("Made it here a");
            playerGame.startGame(myDict, wordLength);
        }
        catch (IOException exception) {
            System.out.println("Made it here b");
            exception.printStackTrace();
        }
        catch (EmptyDictionaryException exception) {
            System.out.println("Made it here c");
            System.out.println(exception.getMessage());
        }

        //loop through the game, as long as guessesRemaining > 0
        while (guessesRemaining > 0) {
//            System.out.println("Made it here D");
            //display number remaining guesses, sorted list of letters guessed so far, partially constructed word
            System.out.println("You have " + guessesRemaining + " guesses remaining.");
//            System.out.println("Made it here d1");
            StringBuilder output = new StringBuilder();
            for (Character letter : playerGame.getGuessedLetters()) {
                output.append(letter);
                output.append(" ");
            }
            System.out.println("Used letters: " + output);
//            System.out.println("Made it here d2");
            System.out.println("Word: " + playerGame.partialGuess);

//            System.out.println("Made it here e");
            try {
//                System.out.println("Made it here f");
                //prompt player for next letter guess--check that guess is valid letter AND not already guessed
                Scanner userInput = new Scanner(System.in);
                System.out.println("Enter your next guess: ");
                System.out.println("Made it here i");
                String userGuess = userInput.next();
                System.out.println("Made it here j");
                userGuess = userGuess.toLowerCase();
                System.out.println("Made it here k");
                while (!userGuess.matches("^[a-z]")) {
                    System.out.println("Please enter a single letter between a-z");
                    System.out.println("Enter your next guess: ");
                    userGuess = userInput.next();
                    userGuess = userGuess.toLowerCase();
                }
                System.out.println("Made it here k1");
                SortedSet<Character> currSetOfGuesses = playerGame.getGuessedLetters();
                System.out.println("Made it here k2");
                while (currSetOfGuesses.contains(userGuess.charAt(0))) {
                    System.out.println("Please enter a letter that has not already been guessed.");
                    System.out.println("Enter your next guess: ");
                    userGuess = userInput.next();
                    userGuess = userGuess.toLowerCase();
                }
                //run makeGuess to identify largest subset of words with or without that letter ... report back to player
                System.out.println("Made it here l");
                System.out.println("Guessing with char " + userGuess);
                playerGame.makeGuess(userGuess.charAt(0));
                System.out.println("Made it here b");
                if (!playerGame.partialGuess.contains(userGuess)) {
                    guessesRemaining -= 1;
                    System.out.println("Sorry, there are no " + userGuess + "'s in the word.\n\n");
                }
                else {
                    int letterCount = 0;
                    for (int i = 0; i < playerGame.partialGuess.length(); i++) {
                        System.out.println("Made it here c");
                        char c = playerGame.partialGuess.charAt(i);
                        if (userGuess.length() > 0) {
                            System.out.println("Made it here d");
                            if (c == userGuess.charAt(0)) {
                                System.out.println("Made it here e");
                                letterCount += 1;
                            }
                        }
                    }
                    if (letterCount == 1) {
                        System.out.println("Yes, there is 1 " + userGuess + "\n\n");
                    }
                    else {
                        System.out.println("Yes, there are " + letterCount + " " + userGuess + "'s in the word.\n\n");
                    }
                }
            }
            catch (GuessAlreadyMadeException exception){
                System.out.println("Made it here g");
                System.out.println(exception.getMessage());
            }
            //repeat until guesses == 0 or subset is only one word
        }
        System.out.println("You lose!\nThe word was: " + playerGame.validWords.last());
    }


}
