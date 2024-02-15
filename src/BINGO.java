import java.io.IOException;
import java.util.Scanner;
import java.util.Random;

public class BINGO {
    // GLOBAL VARIABLES
    static Random rand = new Random();
    static Scanner scanner = new Scanner(System.in);
    static final String BINGO = "BINGO";
    static final int ASCIIMIN = 1, ASCIIMAX = 75;
    static String rolledNumbersRepr = "";
    static String bingoCardRepr = "";
    static String bingoWinningPattern;

    public static void main(String[] args) throws IOException, InterruptedException {
        // WELCOME SCREEN
        System.out.println("Tara BINGO!");
        System.out.println("Press ENTER to play.");
        scanner.nextLine();

        // HELP MODULE
//        playTutorial();

        // INITIALIZATION OF VARIABLES
        createBingoCard();

        // MAIN GAME LOOP
        letsPlayBingo();

        String numbers = "";
        String alreadyRolledNumbers = "";
    }

    static void letsPlayBingo() throws IOException, InterruptedException {
        char letter;
        char randomNumberRepr;
        int randomNumber;

        while (true) {
            randomNumberRepr = getRandomNumberRepr(0, ASCIIMAX);
            randomNumber = randomNumberRepr - 33;

            if (rolledNumbersRepr.contains(randomNumberRepr+"")) {
                continue;
            } else if (randomNumber > 1 && randomNumber <= 15) {
                letter = 'B';
            } else if (randomNumber > 15 && randomNumber <= 30) {
                letter = 'I';
            } else if (randomNumber > 30 && randomNumber <= 45) {
                letter = 'N';
            } else if (randomNumber > 45 && randomNumber <= 60) {
                letter = 'G';
            } else {
                letter = 'O';
            }

            System.out.println("Taya taya... (Press ENTER to roll) ");
            scanner.nextLine();
            System.out.println("Sa letra sang " + letter + "... " + randomNumber + "!");
            if (bingoCardRepr.contains(randomNumberRepr+"")) {
                System.out.println("May " + randomNumber + "!\n");
            } else {
                System.out.println("Wala " + randomNumber + ".\n");
            }

            rolledNumbersRepr += randomNumberRepr + " ";
            printBingoCard();
            System.out.println("\nPress enter to roll again. ");
            scanner.nextLine();
            cls();
        }
    }

    static void createBingoCard() {
        char randNumberRepr;
        String sep = "\t\t";

        for (int row = 0; row < 5; row++) {
            int col = 0;
            while (col < 5) {
                if (row == 2 && col == 2) {
                    bingoCardRepr += ' ' + sep;
                } else {
                    randNumberRepr = getRandomNumberRepr((col + 1) * 15, col * 15 + 2);
                    if (bingoCardRepr.contains(randNumberRepr+"")) {
                        continue;
                    } else {
                        bingoCardRepr += randNumberRepr + sep;
                    }
                }
                col++;
            }
            bingoCardRepr += "\n\n";
        }
    }

    static void printBingoCard() {
        char currentNumRepr;
        int currentNum;

        for (int i = 0; i < bingoCardRepr.length(); i++) {
            currentNumRepr = bingoCardRepr.charAt(i);
            currentNum = currentNumRepr - 33;

            if (currentNumRepr == ' ') {
                System.out.print("B");
            } else if (currentNumRepr == '\n' || currentNumRepr == '\t') {
                System.out.print(currentNumRepr);
            } else if (rolledNumbersRepr.contains(currentNumRepr+"")) {
                System.out.print("(" + currentNum + ")");
            } else {
                System.out.print(currentNum);
            }
        }
    }

    static char getRandomNumberRepr(int min, int max) {
        return (char) ((rand.nextInt(max - min) + min) + 33);
    }

    static int getNumberFromRepr(char repr) {
        return repr - 33;
    }

    static void contains(char repr) {
        int i;
    }

    static void cls() throws IOException, InterruptedException {
        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
    }

    static void playTutorial() throws IOException, InterruptedException {
        System.out.println("Hello! Welcome to the BINGO tutorial! (ENTER to continue)");
        scanner.nextLine();
        System.out.print("Let's start with the mechanics:\nBINGO is a game of chance. (ENTER)");
        scanner.nextLine();
        System.out.println("Once every turn, a number is rolled randomly. (ENTER)");
        scanner.nextLine();
        System.out.print("""
                On a BINGO card that looks something like this:
                12\t\t22\t\t43\t\t51\t\t72
                5\t\t23\t\t35\t\t57\t\t61
                9\t\t28\t\tB\t\t48\t\t69
                11\t\t29\t\t41\t\t49\t\t62
                6\t\t19\t\t42\t\t50\t\t65
                if the rolled number is present, the number is marked. (ENTER)
                """);
        scanner.nextLine();
        System.out.print("""
                The middle, denoted B, is a sort of free square.
                Consider it already marked. (ENTER)
                """);
        scanner.nextLine();
        System.out.print("""
                The game is won if five (5) marks in a row is achieved,
                either vertically, horizontally, or diagonally. (ENTER)
                """);
        scanner.nextLine();
        System.out.println("Good luck and have fun! (ENTER)");
        scanner.nextLine();
        cls();
    }
}