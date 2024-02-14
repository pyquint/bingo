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

    public static void main(String[] args) {
        // WELCOME SCREEN

        // HELP MODULE

        // INITIALIZATION OF VARIABLES
        createBingoCard();

        // MAIN GAME LOOP
        letsPlayBingo();

        // 5*5 grid, center is blank
        String numbers = "";
        String alreadyRolledNumbers = "";
    }

    static void letsPlayBingo() {
        char randomNumberRepr;
        int randomNumber;

        while (true) {
            randomNumberRepr = getRandomNumberRepr(0, 75);
            randomNumber = randomNumberRepr - 33;
            if (rolledNumbersRepr.contains(randomNumberRepr+"")) {
                continue;
            }
            System.out.println("Taya taya... " + randomNumber + "!");
            if (bingoCardRepr.contains(randomNumberRepr+"")) {
                System.out.println("May ara " + randomNumber + ".");
            } else {
                System.out.println("Wala " + randomNumber + ".");

            }
            rolledNumbersRepr += (randomNumberRepr) + " ";
            printBingoCard();
            System.out.println("\nPress enter to roll again. ");
            scanner.nextLine();
            System.out.println(rolledNumbersRepr);
        }
    }

    static void createBingoCard() {
        int max, min;
        char randNumberRepr;
        String sep = "\t\t\t";

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; ) {
                if (i == 2 && j == 2) {
                    bingoCardRepr += " " + sep;
                    j++;
                    continue;
                }
                max = (j + 1) * 15;
                min = j * 15 + 1;
                randNumberRepr = getRandomNumberRepr(min, max); // (rand.nextInt(107 - 33) + 33);
                if (bingoCardRepr.indexOf(randNumberRepr) == -1) {
                    bingoCardRepr += randNumberRepr + sep;
                    j++;
                }
            }
            bingoCardRepr += "\n";
        }
    }

    static void printBingoCard() {
        char currentNumRepr;
        int currentNum;

        for (int i = 0; i < bingoCardRepr.length(); i++) {
            currentNumRepr = bingoCardRepr.charAt(i);
            currentNum = currentNumRepr - 33;

            if (currentNumRepr == '\n') {
                System.out.println();
            }
            else if (currentNumRepr == ' ') {
                System.out.print("B");
            } else if (rolledNumbersRepr.contains(currentNumRepr+"")) {
                System.out.print("(" + currentNum + ")");
            } else if (currentNumRepr >= '!' && currentNumRepr <= '~') {
                System.out.print(currentNum);
            } else {
                System.out.print(currentNumRepr);
            }
        }
    }

    static char getRandomNumberRepr(int min, int max) {
        return (char) ((rand.nextInt(max - min) + min) + 33);
    }

    static void contains(char repr) {
        int i;
    }
}