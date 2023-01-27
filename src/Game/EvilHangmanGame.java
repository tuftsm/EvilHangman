package Game;

import com.sun.source.tree.Tree;
import org.junit.platform.commons.util.StringUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.*;

public class EvilHangmanGame implements IEvilHangmanGame {

    TreeSet<String> validWords = new TreeSet<>();
    SortedSet<Character> guessedLetters = new TreeSet<>();
    String partialGuess;

    @Override
    public void startGame(File dictionary, int wordLength) throws IOException, EmptyDictionaryException {
        System.out.println("Made it here 1");
        //clear sets from past runs
        validWords.clear();
        guessedLetters.clear();
        //initialize partialGuess to ----
        String dash = "-";
        partialGuess = dash.repeat(wordLength);
        System.out.println("Made it here 2");
        //check that dictionary isn't empty, if is--throw EmptyDictionaryException
         if (dictionary.length() == 0) {
             System.out.println("Made it here 3");
             throw new EmptyDictionaryException("The dictionary has no words.");
         }
         else if (dictionary.length() > 0) {
             System.out.println("Made it here 4");
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
        System.out.println(validWords.first());
        for (String word : validWords) {
//            System.out.println("aaa");
//            System.out.println(validWords);
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
                    int lsFirstIndex = -1;
                    int eFirstIndex = -1;

//                    char[] lsPatternAsChar = largestSet.getKey().toCharArray();
//                    char[] ePatternAsChar = entry.getKey().toCharArray();
                    String lsPattern = largestSet.getKey().toString();
                    for (int i = 0; i < lsPattern.length(); i++) {
                        char c = lsPattern.charAt(i);
                        if (c == guess) {
                            lsCount +=1;
                            if (lsFirstIndex == -1) {
                                lsFirstIndex = i;
                            }
                        }
                    }
                    String ePattern = entry.getKey().toString();
                    for (int i = 0; i < ePattern.length(); i++) {
                        char c = ePattern.charAt(i);
                        if (c == guess) {
                            eCount +=1;
                            if (eFirstIndex == -1) {
                                eFirstIndex = i;
                            }
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
                        if (eFirstIndex > lsFirstIndex) {
                            largestSet = entry;
                        }
                        else if (lsFirstIndex > eFirstIndex) {
                            largestSet = largestSet;
                        }
                    }
                }
            }
        }

        //assign validWords to largestSet ... the key will be the pattern
        validWords = largestSet.getValue();
        partialGuess = largestSet.getKey();
//        System.out.println(validWords);
        //return validWords
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

//
//package Game;
//
//import java.io.File;
//import java.io.FileReader;
//import java.io.IOException;
//import java.util.*;
//
//public class EvilHangmanGame implements IEvilHangmanGame {
//    TreeSet<String> dictionaryWords = new TreeSet<>();
//    SortedSet<Character> guessedLetters = new TreeSet<>();
//    String finalWord, partialGuess;
//
//    public EvilHangmanGame() { }
//
//    @Override
//    public void startGame(File dictionary, int wordLength) throws IOException, EmptyDictionaryException {
//        // Clear out previously used variable of any values
//        guessedLetters.clear();
//        dictionaryWords.clear();
//        partialGuess = new String(new char[wordLength]).replace('\0', '-');
//        // If the dictionary file is not empty
//        if(dictionary.length() > 0) {
//            // Scan the file
//            try(Scanner sc = new Scanner(new FileReader(dictionary))) {
//                // Only store the words of the user given size
//                while(sc.hasNext()) {
//                    String curWord = sc.next();
//                    if(curWord.length() == wordLength) dictionaryWords.add(curWord);
//                }
//                // If no words are found in the dictionary, throw this exception
//                if(dictionaryWords.size() == 0) throw new EmptyDictionaryException("Word Not Found In Dictionary");
//            }
//        }
//        // If the dictionary is really empty, throw this exception
//        else throw new EmptyDictionaryException("There are no words in your dictionary");
//
//        // OUTPUT FOR DEBUGGING
////        for(Map.Entry<String, String> entry: words.entrySet()) System.out.printf("Word: %s  Pattern: %s\n", entry.getKey(), entry.getValue());
//    }
//
//    @Override
//    public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException {
//        TreeMap<String, TreeSet<String>> wordPartitions = new TreeMap<>();
//        char guessLC = Character.toLowerCase(guess);
//        // If the user has made guesses before
//        if(!guessedLetters.isEmpty()) {
//            // If the uses guesses a previously guessed letter, throw this exception
//            if(guessedLetters.contains(guessLC)) throw new GuessAlreadyMadeException("You have already guessed this character\n");
//        }
//        // Add the letter to the set of guessed letters
//        guessedLetters.add(guessLC);
//
//        // Make a pattern for every word and put it in the map with the relative words
//        for(String s : dictionaryWords) {
//            // Save the word as a set to be added as/or to the set in the map
//            TreeSet<String> w = new TreeSet<>();
//            w.add(s);
//            // Get the pattern
//            String pattern = makePattern(s, guessLC);
//            // If that pattern isn't already in the map, put it in the map
//            if(!wordPartitions.containsKey(pattern)) wordPartitions.put(pattern, w);
//                // Otherwise, add the words the the relevant set
//            else wordPartitions.get(pattern).addAll(w);
//        }
//
//        // We're looking for the largest set here
//        Map.Entry<String, TreeSet<String>> maxSet = null;
//        // Go through each of the set of words in the map and check the size
//        for(Map.Entry<String, TreeSet<String>> entry: wordPartitions.entrySet()) {
////            System.out.printf("Pattern: %s  Words: %s\n", entry.getKey(), entry.getValue());
//            // Assign the largest set entry to the maxSet
//            if(maxSet == null || entry.getValue().size() > maxSet.getValue().size()) maxSet = entry;
//            // If there are two partitions that are the same size
//            if(entry.getValue().size() == maxSet.getValue().size()) {
//                // If one of the partitions doesn't have the character, choose it
//                if(!entry.getKey().contains(Character.toString(guessLC))) maxSet = entry;
//                    // If every pattern contains that character
//                else if(entry.getKey().contains(Character.toString(guessLC)) && maxSet.getKey().contains(Character.toString(guessLC))){
//                    // Find the one with the least occurrences
//                    long countA = entry.getKey().chars().filter(ch -> ch == guessLC).count();
//                    long countB = maxSet.getKey().chars().filter(ch -> ch == guessLC).count();
//                    if(countA < countB) maxSet = entry;
//                        // If they have the same amount of occurrences
//                    else if(countA == countB) {
//                        // Choose the one with the rightmost guessed letter
//                        if(entry.getKey().lastIndexOf(guessLC) > maxSet.getKey().lastIndexOf(guessLC)) maxSet = entry;
//                    }
//                }
//            }
//        };
//
//        // Using the largest entry from our map
//        assert maxSet != null;
//        // Add the words to be our new dictionary to pick from
//        dictionaryWords = maxSet.getValue();
//        partialGuess = maxSet.getKey();
//
////        for(String s : dictionaryWords) System.out.println(s);
//
//        // Return our new list of words to play with
//        return dictionaryWords;
//    }
//
//    public String makePattern(String s, char guess) {
//        char[] stringCharArray = s.toCharArray();
//        StringBuilder sb = new StringBuilder();
//        for(char c : stringCharArray) {
//            if(c != guess) c = '-';
//            sb.append(c);
//        }
//        return sb.toString();
//    }
//
//    @Override
//    public SortedSet<Character> getGuessedLetters() { return this.guessedLetters; }
//}