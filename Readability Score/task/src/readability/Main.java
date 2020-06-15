package readability;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {


    static int numOfCharacters;
    static int numOfSentences;
    static int numOfWords;
    static int numOfSyllables;
    static int numOfPolysyllables;
    static String input;

    public static void main(String[] args) {

        //Determine the source of input - passed as a filepath argument, or entered as text
        input = getInput(args);

        //calculate input info
        numOfCharacters = input.replaceAll(" ", "").length();
        String[] sentences = input.split(("[.?!]"));
        numOfSentences = sentences.length;

        for (String sentence : sentences) {
            String[] currentSentenceWords = sentence.trim().split(" ");
            numOfWords += currentSentenceWords.length;
            difficultMethodForSyllables(currentSentenceWords);
        }

        //Print input information, get input on what index(es) to print
        printTextInfo();
        System.out.print("Enter the score you want to calculate ARI, FK, SMOG, CL, all): ");
        String command = new Scanner(System.in).next();
        System.out.println();
        processCommand(command);
    }

    static String getInput(String[] args) {
        String input = "";
        if (args.length > 0) {
            File file = new File(args[0]);
            try (Scanner scan = new Scanner(file)) {

                while (scan.hasNextLine()) {
                    input = input.concat(scan.nextLine());
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            Scanner scan = new Scanner(System.in);
            input = scan.nextLine();
        }
        return input;
    }

    static void processCommand(String command) {
        switch (command) {
            case "ARI":
                automatedReadabilityIndex();
                break;
            case "FK":
                fleschKincaid();
                break;
            case "SMOG":
                smogIndex();
                break;
            case "CL":
                colemanLiauIndex();
                break;
            case "all":
                automatedReadabilityIndex();
                fleschKincaid();
                smogIndex();
                colemanLiauIndex();
                System.out.println();
                System.out.printf("This text should be understood in average by %.2f year olds.", calculateAverageAge());
                break;
            default:
                System.out.println("Invalid command.");
                break;
        }
    }

    static int getRecAge(double score) {
        switch ((int) Math.round(score)) {
            case 1:
                return 6;
            case 2:
                return 7;
            case 3:
                return 9;
            case 4:
                return 10;
            case 5:
                return 11;
            case 6:
                return 12;
            case 7:
                return 13;
            case 8:
                return 14;
            case 9:
                return 15;
            case 10:
                return 16;
            case 11:
                return 17;
            case 12:
                return 18;
            case 13:
                return 24;
            case 14:
                return 25;
            default:
                return 0;

        }
    }

    /*
     *1. Count the number of vowels in the word.
     *2. Do not count double-vowels (for example, "rain" has 2 vowels but is only 1 syllable)
     *3. If the last letter in the word is 'e' do not count it as a vowel (for example, "side" is 1 syllable)
     *4. If at the end it turns out that the word contains 0 vowels, then consider this word as 1-syllable.
     * */
    static void difficultMethodForSyllables(String[] currentSentenceWords) {
        for (int i = 0; i < currentSentenceWords.length; i++) {
            String currentWord = currentSentenceWords[i].replaceAll("e$", "");
            int currentSyllables = 0;

            for (int j = 0; j < currentWord.length(); j++) {
                if ((String.valueOf(currentWord.charAt(j))).toLowerCase().matches(("[aeiouy]"))) {
                    if (j == 0) {
                        currentSyllables++;
                    } else if (j != currentWord.length() - 1 && !String.valueOf(currentWord.charAt(j - 1)).toLowerCase().matches("[aeiouy]")) {
                        currentSyllables++;
                    }
                }
            }
            if (currentSyllables > 2) {
                numOfPolysyllables++;
            }
            if (currentSyllables == 0) {
                numOfSyllables++;
            } else {
                numOfSyllables += currentSyllables;
            }
        }
    }

    static void printTextInfo() {
        System.out.println("The text is:");
        System.out.println(input);
        System.out.println("");
        System.out.println("Words: " + numOfWords);
        System.out.println("Sentences: " + numOfSentences);
        System.out.println("Characters: " + numOfCharacters);
        System.out.println("Syllables: " + numOfSyllables);
        System.out.println("Polysyllables: " + numOfPolysyllables);
    }

    static double calculateARI() {
        return 4.71 * ((double) numOfCharacters / numOfWords) + 0.5 * ((double) numOfWords / numOfSentences) - 21.43;
    }

    static void automatedReadabilityIndex() {
        double ariValue = calculateARI();
        System.out.printf("Automated Readability Index: %.2f (about %s year olds)\n", ariValue, getRecAge(ariValue));
    }

    static double calculateFleschKincaid() {
        return 0.39 * (double) numOfWords / numOfSentences + 11.8 * (double) numOfSyllables / numOfWords - 15.59;
    }

    static void fleschKincaid() {
        double value = calculateFleschKincaid();
        System.out.printf("Flesch–Kincaid readability tests: %.2f (about %s year olds) \n", value, getRecAge(value));
    }

    static double calculateSMOG() {
        return 1.043 * Math.sqrt(numOfPolysyllables * 30 / numOfSentences) + 3.1291;
    }

    static void smogIndex() {
        double value = calculateSMOG();
        System.out.printf("Simple Measure of Gobbledygook: %.2f (about %s year olds) \n", value, getRecAge(value));
    }

    static double calculateColemanLiau() {
        double L = (double) numOfCharacters / numOfWords * 100;
        double S = (double) numOfSentences / numOfWords * 100;
        return 0.0588 * L - 0.296 * S - 15.8;
    }

    static void colemanLiauIndex() {
        double value = calculateColemanLiau();
        System.out.printf("Coleman–Liau index: %.2f (about %s year olds) \n", value, getRecAge(value));
    }

    static double calculateAverageAge() {
        return ((double) getRecAge(calculateARI()) + getRecAge(calculateColemanLiau()) + getRecAge(calculateFleschKincaid()) + getRecAge(calculateSMOG())) / 4;
    }
}
