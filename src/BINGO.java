import java.io.IOException;
import java.util.Scanner;
import java.util.Random;

public class BINGO {
    // GLOBAL VARIABLES
    static Random rand = new Random();
    static Scanner scanner = new Scanner(System.in);
    static final String BINGO = "BINGO";
    static final int ASCIIMIN = 32, BINGOMAX = 75;
    static String rolledNumbersRepr = "";
    static String bingoCardRepr = "";
    static String bingoWinningPattern;
    static String sep = "\t\t";

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
            randomNumberRepr = getNumberRepr(getRandomNumber(0, BINGOMAX + 1));
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
            printlnInteractive("Taya taya...");
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
         * bingoCardRepr is a one-directional string which includes five rows (separated by newline) of five characters
         * We cannot naively store the card numbers as numerical values, or verify membership of single-digit numbers.
         * For example: We rolled a '1'. If we use bingoCardRepr.contains('1'), it will look for 1, 12, 13, ..., 21, 31, and so on.
         *
         * One technique I could think of is we could change the way we check the membership of rolled numbers.
         * But as of right now I cannot think of anything about this.
         *
         * What I came up with and ultimately chose was (Repr)esenting the rolled number into an ASCII character.
         * The number is always converted to its associated character, and vice versa.
         * We start the mapping at the 33rd character ('!') up to 108 ('k', 33 + 75 - 1).
         * The SPACE, is reserved to the middle FREE SQUARE of the card.
         *
         * But I am open to suggestions. Is there a better way of doing this?
         */
        char randNumberRepr;
        int min, max;

        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5;) {
                 if (row == 2 && col == 2) {
                    bingoCardRepr += ' ';
                    col++;
                    continue;
                }
                min = col * 15 + 1; max = (col + 1) * 15 + 1;
                randNumberRepr = getNumberRepr(getRandomNumber(min, max));
                if (bingoCardRepr.contains(randNumberRepr+"")) {
                    continue;
                } else {
                    bingoCardRepr += randNumberRepr+"";
                    col++;
                }
            }
            bingoCardRepr += '\n';
        }
    }

    static void printBingoCardRepr() {
        /*
         * Since bingoCardRepr is a 1-dimentional form, there is no need for nested loops.
         * I instead used conditionals to further control the printing of the card.
         *
         * TODO row and column separation
         */
        char currentNumRepr;
        int currentNum;

        System.out.println("B" + sep + "I" + sep + "N" + sep + "G" + sep + "O\n");
        for (int i = 0; i < bingoCardRepr.length(); i++) {
            currentNumRepr = bingoCardRepr.charAt(i);

            if (currentNumRepr == ' ') {
                System.out.print("FS" + sep);
            } else if (currentNumRepr == '\n') {
                System.out.println(currentNumRepr);
            } else {
                currentNum = getReprNumber(currentNumRepr);
                // enclose the number with parentheses if the number is already called out
                System.out.print(rolledNumbersRepr.contains(currentNumRepr+"") ? "(" + currentNum + ")" : currentNum);
                System.out.print(sep);
            }
        }
    }

    static int getRandomNumber(int min, int max) {
        // .nextInt((max-min) + min) --> range between min (inclusive) and max (exclusive)
        return rand.nextInt(max - min) + min;
    }

    static char getNumberRepr(int number) {
        return (char) (number + ASCIIMIN);
    }

    static int getReprNumber(char repr) {
        return repr - ASCIIMIN;
    }

    static void cls() throws IOException, InterruptedException {
        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
    }

    static void printlnInteractive(String s) {
        System.out.println(s + " (ENTER) ");
        scanner.nextLine();
    }

    static void printInteractive(String s) {
        System.out.print(s + " (ENTER) ");
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
