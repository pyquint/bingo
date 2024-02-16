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
        printlnInteractive("""
            _______  ___   __    _  _______  _______  __
            |  _    ||   | |  |  | ||       ||       ||  |
            | |_|   ||   | |   |_| ||    ___||   _   ||  |
            |       ||   | |       ||   | __ |  | |  ||  |
            |  _   | |   | |  _    ||   ||  ||  |_|  ||__|
            | |_|   ||   | | | |   ||   |_| ||       | __
            |_______||___| |_|  |__||_______||_______||__|

            Tara BINGO! (ENTER to continue)""");

        // HELP MODULE
        // playTutorial();

        // INITIALIZATION OF VARIABLES
        createBingoCardRepr();

        // MAIN GAME LOOP
        letsPlayBingo();
    }

    static void letsPlayBingo() throws IOException, InterruptedException {
        char letter;
        char randomNumberRepr;
        int randomNumber;

        while (true) {
            randomNumberRepr = getRandomNumberRepr(0, ASCIIMAX + 1);
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

            printBingoCardRepr();
            printlnInteractive("Taya taya...\n");
            printInteractive("Sa letra sang...");
            printlnInteractive(letter + "!");
            printlnInteractive(randomNumber + "!");
            System.out.println((bingoCardRepr.contains(randomNumberRepr+"") ? "May " : "Wala ") + randomNumber + "!\n");
            rolledNumbersRepr += randomNumberRepr + " ";
            printlnInteractive("\nRoll again >>>");
            cls();
        }
    }

    static void createBingoCardRepr() {
        /*
         * bingoCardRepr is a one-directional string separated by `sep` between numbers and `\n` between rows
         * We cannot naively store the card numbers as they are, or check the card by membership.
         * Single digits cannot be verified if it is on the card.
         * For example: We rolled a '1'. If we use bingoCardRepr.contains('1'), it will accept 1, 12, 13, ..., 21, 22, and so on.
         *
         * One technique I could think is we could change the way we check the membership of rolled numbers.
         * But as of right now I cannot think of anything about this.
         *
         * What I thought of and ultimately chose was (Repr)esenting the rolled number into an ASCII character.
         * The number, when converted, will always be its associated character and vice versa.
         * We start the mapping at the 33rd character, second printable character in ASCII, up to 75 (the maximum in BINGO).
         * The first printable character, the SPACE, is reserved to the middle square of the card.
         *
         * But I am open to suggestions. Is there a bettew way of doing this?
         */
        char randNumberRepr;
        String sep = "\t\t";

        for (int row = 0; row < 5; row++) {
            int col = 0;
            while (col < 5) {
                 if (row == 2 && col == 2) {
                    bingoCardRepr += ' ' + sep;
                } else {
                    randNumberRepr = getRandomNumberRepr(col * 15 + 1, (col + 1) * 15 + 1);
                    if (bingoCardRepr.contains(randNumberRepr+"")) {
                        continue;
                    }
                    bingoCardRepr += randNumberRepr + ((col != 4) ? sep : "");
                }
                col++;
            }
            bingoCardRepr += "\n";
        }
    }

    static void printBingoCardRepr() {
        /*
         * Since bingoCardRepr is a 1-directional form, there is no need for nested loops.
         * I instead used conditionals to further control the printing of the card.
         *
         * Ugly code, need refactoring.
         * TODO row and column separation
         */
        char currentNumRepr;
        int currentNum;

        System.out.println("B\t\tI\t\tN\t\tG\t\tO\n");
        for (int i = 0; i < bingoCardRepr.length(); i++) {
            currentNumRepr = bingoCardRepr.charAt(i);
            currentNum = currentNumRepr - 33;

            if (currentNumRepr == ' ') {
                System.out.print("B");
            } else if (currentNumRepr == '\n') {
                // TODO row separation
                System.out.print(currentNumRepr);
                // System.out.print("-----------------------------------------------------------------------\n");
            } else if (currentNumRepr == '\t') {
                System.out.print(currentNumRepr);
                // TODO column separation
                // if (i % 2 == 1) {
                //     System.out.print("|");
                // }
            } else if (rolledNumbersRepr.contains(currentNumRepr+"")) {
                System.out.print("(" + currentNum + ")");
            } else {
                System.out.print(currentNum);
            }
        }
    }

    static char getRandomNumberRepr(int min, int max) {
        // .nextInt((max-min) + min) --> range between min (inclusive) and max (exclusive)
        return (char) ((rand.nextInt(max - min) + min) + 33);
    }

    static int getNumberFromRepr(char repr) {
        return repr - 33;
    }

    static void cls() throws IOException, InterruptedException {
        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
    }

    static void printlnInteractive(String s) {
        System.out.println(s + " (ENTER)");
        scanner.nextLine();
    }

    static void printInteractive(String s) {
        System.out.print(s + " (ENTER)");
        scanner.nextLine();
    }

    static void playTutorial() throws IOException, InterruptedException {
        cls();
        printlnInteractive("Hello! Welcome to the BINGO tutorial!");
        printInteractive("Let's start with the mechanics.");
        printlnInteractive("BINGO is a game of chance.");
        printlnInteractive("Once every turn, a number is rolled randomly.");
        printlnInteractive("""
                On a BINGO card that looks something like this:
                12\t\t22\t\t43\t\t51\t\t72\n
                5\t\t23\t\t35\t\t57\t\t61\n
                9\t\t28\t\tB\t\t48\t\t69\n
                11\t\t29\t\t41\t\t49\t\t62\n
                6\t\t19\t\t42\t\t50\t\t65\n
                if the rolled number is present, that square gets marked.""");
        printlnInteractive("""
                The middle, denoted B, is a sort of free square.
                Consider it already marked.""");
        printlnInteractive("""
                The game is won if five (5) marks in a row is achieved,
                either vertically, horizontally, or diagonally.""");
        printlnInteractive("Good luck and have fun!");
        cls();
    }
}
