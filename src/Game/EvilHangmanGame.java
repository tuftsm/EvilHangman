package Game;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.*;

public class EvilHangmanGame implements IEvilHangmanGame {

    TreeSet<String> validWords = new TreeSet<>();
    SortedSet<Character> guessedLetters = new TreeSet<>();
    String partialGuess;

    @Override
    public void startGame(File dictionary, int wordLength) throws IOException, EmptyDictionaryException {
        //clear sets from past runs
        validWords.clear();
        guessedLetters.clear();
        //initialize partialGuess to ----
        String dash = "-";
        partialGuess = dash.repeat(wordLength);
        //check that dictionary isn't empty, if is--throw EmptyDictionaryException
         if (dictionary.length() == 0) {
             throw new EmptyDictionaryException("The dictionary has no words.");
         }
         else if (dictionary.length() > 0) {
             //scan file, adding each word of wordLength to validWords Set
            Scanner dict = new Scanner(new FileReader(dictionary));
            String word;

             while (dict.hasNext()) {
                 word = dict.next();
                 word = word.toLowerCase();
                 int length = word.length();
                 if (length == wordLength) {
                     validWords.add(word);
                 }
             }
         }
         //if no words of wordLength in validWords, throw EmptyDictionaryException
        if (validWords.size() == 0) {
            throw new EmptyDictionaryException("There are no words of length " + wordLength + " in the dictionary.");
        }
    }

    @Override
    public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException {
        TreeMap<String, TreeSet<String>> wordMap = new TreeMap<>();
        //convert guess to lowerCase char
        guess = Character.toLowerCase(guess);
        String guessString = Character.toString(guess);

        //check if char is in guessedLetters...if is, throw GuessAlreadyMadeException...if not, add char to guessedLetters
        if (guessedLetters.contains(guess)) {
            throw new GuessAlreadyMadeException("This character has already been guessed.");
        }
        else {
            guessedLetters.add(guess);
        }
        //loop through all words in validWords
        for (String word : validWords) {
            //make new subset, add word to subset
            TreeSet<String> subset = new TreeSet<>();
            subset.add(word);

            //run pattern making algorithm
                //convert word to array of characters
                char[] wordAsChars = word.toCharArray();
                StringBuilder sb = new StringBuilder();
                //for each char in array, if char == guessedLetter -> append char to StringBuilder, else if char != guessedLetter -> append '-' to StringBuilder
                for (char myChar : wordAsChars) {
                    if (this.guessedLetters.contains(myChar)) {
                        sb.append(myChar);
                    }
                    else if (myChar != guess) {
                        sb.append('-');
                    }
                }
                String pattern = sb.toString();
                partialGuess = pattern;
            //if that pattern is in wordMap, add word to appropriate pattern in wordMap
            if (wordMap.containsKey(pattern)) {
                wordMap.get(pattern).addAll(subset);
            }
            //else, put in wordMap
            else {
                wordMap.put(pattern, subset);
            }
        }

        Map.Entry<String, TreeSet<String>> largestSet = null;
        //loop through each entry in wordMap
        for (Map.Entry<String, TreeSet<String>> entry : wordMap.entrySet()) {
            //find entry with the largest size, set as largestSet
            if (largestSet == null) {
                largestSet = entry;
            }
            else if (entry.getValue().size() > largestSet.getValue().size()) {
                largestSet = entry;
            }
            //tiebreakers
            else if (entry.getValue().size() == largestSet.getValue().size()) {
                //select set which does not contain the guessed letter
                if (entry.getKey().contains(guessString) && !largestSet.getKey().contains(guessString)) {
                    largestSet = largestSet;
                }
                else if (largestSet.getKey().contains(guessString) && !entry.getKey().contains(guessString)) {
                    largestSet = entry;
                }
                //compare which set has the least amount of guessed letters
                else {
                    int lsCount = 0;
                    int eCount = 0;

                    String lsPattern = largestSet.getKey().toString();
                    for (int i = 0; i < lsPattern.length(); i++) {
                        char c = lsPattern.charAt(i);
                        if (c == guess) {
                            lsCount +=1;
                        }
                    }
                    String ePattern = entry.getKey().toString();
                    for (int i = 0; i < ePattern.length(); i++) {
                        char c = ePattern.charAt(i);
                        if (c == guess) {
                            eCount +=1;
                        }
                    }
                    //set largestSet to one with fewer instances of guessed letter
                    if (eCount < lsCount) {
                        largestSet = entry;
                    }
                    else if (lsCount < eCount) {
                        largestSet = largestSet;
                    }
                    //compare which set has guessed letter further to the right
                    else {
                        int eIndex = 0;
                        int lsIndex = 0;
                        for (int i = 0; eIndex == lsIndex; i++) {
                            char eChar = ePattern.charAt(i);
                            if (eChar == guess) {
                                eIndex += 1;
                            }
                            char lsChar = lsPattern.charAt(i);
                            if (lsChar == guess) {
                                lsIndex += 1;
                            }
                            if (lsIndex < eIndex) {
                                largestSet = largestSet;
                            }
                            if (eIndex < lsIndex) {
                                largestSet = entry;
                            }
                        }
                    }
                }
            }
        }
        //assign validWords to largestSet ... the key will be the pattern
        validWords = largestSet.getValue();
        partialGuess = largestSet.getKey();

        return validWords;
    }

    @Override
    public SortedSet<Character> getGuessedLetters() {
        return this.guessedLetters;
    }

    public String getCurrentPattern() {
        return this.partialGuess;
    }
}
