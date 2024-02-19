import java.io.IOException;
import java.util.Scanner;
import java.util.Random;

public class BINGO {
    // GLOBAL VARIABLES
    static Random rand = new Random();
    static Scanner scanner = new Scanner(System.in);

    static final int ASCIIMIN = 32, BINGOMAX = 75, MIDDLE = 12, LENGTH = 25;
    static String ROLLED_NUMBERS_REPR = "", bingoCardRepr = "";
    static String bingoCardPattern = "", BINGO_WINNING_PATTERNS = "";
    static String sep = "\t";

    static String BINGOASCII = """
        _______  ___   __    _  _______  _______  __
        |  _    ||   | |  |  | ||       ||       ||  |
        | |_|   ||   | |   |_| ||    ___||   _   ||  |
        |       ||   | |       ||   | __ |  | |  ||  |
        |  _   | |   | |  _    ||   ||  ||  |_|  ||__|
        | |_|   ||   | | | |   ||   |_| ||       | __
        |_______||___| |_|  |__||_______||_______||__|
            """;

    // SHAKER TOO BIG?
    static String  BINGOSHAKE = """
      @@@
    @@  @@
   @@   @@@@
 @@    @   @@
@@   @@     @@@
@@@@          @@                             @@
   @@          +@@               @@@@@@        @@
     @@           @@@     @@@@@@@    @@          @
       @@           @@@@@@-         @  @          @@
         @@         @@             @@  @@           @
           @@       .             @@    @            @
             @@@@@@              @       @
             @                 @@        =@
             @                @@          @
             @               @@            @%
             @             @@               @
             @          @@@                  @
            @@       @@@                      @
            @     @@@                         @@
  @         @@ @@@@                           @@
   #@       @@@                             @@
     @         @@@@                       @
      @            @@@@                 @@
        @@@            @@@@@         @@
           @@@               @@@@@@@@
""";

    public static void main(String[] args) throws IOException, InterruptedException {
        // WELCOME SCREEN
        System.out.println(BINGOSHAKE);
        System.out.println(BINGOASCII);
        printInteractive("Tara BINGO!");

        // INITIALIZATION OF VARIABLES
        initializeBingoWinningPattern();
        createBingoCardRepr();

        // PATTERN CREATION
        String response;
        do {
            System.out.print("Do you want to create a custom winning pattern? (y/n): ");
            response = scanner.nextLine().strip().toLowerCase();
        } while (!(response != "y" || response != "n"));

        if (response.equals("y")) patternCreation();

        // HELP MODULE
        // playTutorial();

        // MAIN GAME LOOP
        letsPlayBingo();
    }

    static void letsPlayBingo() throws IOException, InterruptedException {
        char letter;
        char randomNumberRepr;
        int randomNumber;
        String membership;

        cls();
        boolean isPlaying = true;

        while (isPlaying) {
            do {
                randomNumberRepr = getNumberRepr(getRandomNumber(1, BINGOMAX + 1));
            } while (ROLLED_NUMBERS_REPR.contains(randomNumberRepr+""));

            randomNumber = randomNumberRepr - ASCIIMIN;
            membership = (bingoCardRepr.contains(randomNumberRepr+"")) ? "May " : "Wala ";

            if (randomNumber >= 1 && randomNumber <= 15) {
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

            updateBingoCardPattern();

            if (winningPatternContainsCardPattern()) {
                System.out.println("BINGO!!!");
                printBingoCardRepr();
                isPlaying = false;
                break;
            };

            // System.out.print(BingoShake);
            printBingoCardRepr();

            printlnInteractive("\nTaya taya...");
            printInteractive("Sa letra sang...");
            printInteractive(letter + "!");
            printInteractive(randomNumber + "!");
            System.out.println(membership + randomNumber + "!");

            ROLLED_NUMBERS_REPR += randomNumberRepr + " ";
            printInteractive("\nRoll again >>>");
            cls();
        }
    }

    static void createBingoCardRepr() {
        /*
        * bingoCardRepr is a one-directional string which includes five rows (separated by newline) of five characters.
         * We cannot naively store the card numbers as numerical values, or verify membership of single-digit numbers.
         * For example: We rolled a '1'. If we use bingoCardRepr.contains('1'), it will look for 1, 12, 13, ..., 21, 31, and so on.
         *
         * One technique I could think of is we could change the way we check the membership of rolled numbers.
         * But as of right now I cannot think of anything about this.
         *
         * What I came up with and ultimately chose was (Repr)esenting the rolled number into an ASCII character.
         * The number is always converted to its associated character, and vice versa.
         * We start the mapping at the 33rd character ('!') up to 107 ('k', 33 + 75 - 1).
         * The SPACE, is reserved to the middle FREE SQUARE of the card.
         *
         * But I am open to suggestions. Is there a better way of doing this?
         */
        char randNumberRepr;
        int min, max;

        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5; col++) {
                 if (row == 2 && col == 2) {
                    bingoCardRepr += ' ';
                    continue;
                }
                min = col * 15 + 1;
                max = (col + 1) * 15 + 1;
                do {
                    randNumberRepr = getNumberRepr(getRandomNumber(min, max));
                } while (bingoCardRepr.contains(randNumberRepr+""));
                bingoCardRepr += randNumberRepr+"";
            }
        }
    }

    static void printBingoCardRepr() {
        /*
         * Since bingoCardRepr is a 1-dimentional form, there is no need for nested loops.
         * I instead used conditionals to further control the printing of the card.
         */
        char currentNumRepr;
        int currentNum;

        System.out.println("B" + sep + "I" + sep + "N" + sep + "G" + sep + "O");

        for (int i = 0; i < LENGTH; i++) {
            currentNumRepr = bingoCardRepr.charAt(i);

            if (currentNumRepr == ' ') {
                System.out.print("FS" + sep);
            } else {
                currentNum = getReprNumber(currentNumRepr);
                // enclose the number with parentheses if the number is already called out, else print as it is
                System.out.print(ROLLED_NUMBERS_REPR.contains(currentNumRepr+"") ? "(" + currentNum + ")" : currentNum);
                System.out.print(sep);
            }
            if (i % 5 == 4) {
                System.out.print('\n');
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
        // QUEENIELYN
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

    static void patternCreation() throws IOException, InterruptedException {
        /*
         * A `pattern` is a string of '*'s and '-', where '*' is a marked square, while '-' is the default.
         * All winning patterns, if there are multiple (to be implemented), are stored in a single `bingoWinningPatterns` String, separated by  ',' between.
         *
         * Player bingo cards are also converted into patterns, and we can simply iterate the cardPattern against the winningPatterns if the marks are matching.
         * Feeling proud...
         */

        // TOOL TUTORIAL
        patternCreationTutorial();
        String response;
        boolean inTool = true;
        int currentSelection = 0;

        while (inTool) {
            cls();

            // PRINTING THE PATTERN MAKER CARD
            System.out.println("PATTERN MAKER TOOL\n");
            System.out.println("B" + sep + "I" + sep + "N" + sep + "G" + sep + "O");
            for (int j = 0; j < LENGTH; j++) {
                if (j == currentSelection) {
                    System.out.print("[" + BINGO_WINNING_PATTERNS.charAt(j) + "]");
                } else {
                    System.out.print(BINGO_WINNING_PATTERNS.charAt(j));
                }

                if ((j+1) % 5 == 0) {
                    System.out.println('\n');
                } else {
                    System.out.print(sep);
                }
            }
            System.out.println();

            while (true) {
                System.out.println("[wasd] Move current selection");
                System.out.println("[q] Mark current selection");
                System.out.println("[z] Mark whole row");
                System.out.println("[x] Mark whole column");
                System.out.println("[e] Finish and exit");
                System.out.println("[r] Reset");

                response = scanner.nextLine().toLowerCase().strip();

                if (response.equals("q")) {
                    BINGO_WINNING_PATTERNS = replace(BINGO_WINNING_PATTERNS, currentSelection, "*");
                } else if (response.equals("z")) {
                    markWinningPatternRow(currentSelection);
                } else if (response.equals("x")) {
                    markWinningPatternColumn(currentSelection);
                } else if (response.equals("e")) {
                    System.out.println("Exiting pattern tool...");
                    inTool = false;
                } else if (response.equals("r")) {
                    initializeBingoWinningPattern();
                } else if (response.equals("w") && currentSelection > 4) {
                    currentSelection -= 5;
                } else if (response.equals("a") && currentSelection % 5 > 0) {
                    currentSelection -= 1;
                } else if (response.equals("s") && currentSelection <= 20) {
                    currentSelection += 5;
                } else if (response.equals("d") && currentSelection % 5 < 4) {
                    currentSelection += 1;
                } else {
                    System.out.println("boop");
                    break;
                }
                break;
            }
        }
        BINGO_WINNING_PATTERNS += ',';
    }

    static void initializeBingoWinningPattern() {
        // INITIALIZE WINNING PATTERN
        BINGO_WINNING_PATTERNS = "";
        for (int i = 0; i < LENGTH; i++) {
            BINGO_WINNING_PATTERNS += ((i == MIDDLE) ? "*" : "-");
        }
    }

    static void updateBingoCardPattern() {
        bingoCardPattern = "";
        for (int i = 0; i < bingoCardRepr.length(); i++) {
            char currentChar = bingoCardRepr.charAt(i);
            if (currentChar == '\n') continue;
            bingoCardPattern += (ROLLED_NUMBERS_REPR.contains(currentChar+"")) ? '*' : '-';
        }
    }

    static boolean winningPatternContainsCardPattern() {
        boolean won = true;
        for (int i = 0; i < LENGTH; i++) {
            if (i == MIDDLE) continue;
            char currCard = bingoCardPattern.charAt(i);
            char currPatt = BINGO_WINNING_PATTERNS.charAt(i);
            if (currPatt == '*' && currCard != '*') return false;
        }
        return won;
    }

    static void markWinningPatternRow(int currentSelection) {
        int leftMostIndex = 0;
        if  (currentSelection <= 4) {
            leftMostIndex = 0;
        } else if (currentSelection <= 9) {
            leftMostIndex = 5;
        } else if (currentSelection <= 14) {
            leftMostIndex = 10;
        } else if (currentSelection <= 19) {
            leftMostIndex = 15;
        } else if (currentSelection <= 24) {
            leftMostIndex = 20;
        }
        BINGO_WINNING_PATTERNS = BINGO_WINNING_PATTERNS.substring(0, leftMostIndex) + "*****" + BINGO_WINNING_PATTERNS.substring(leftMostIndex+5);
    }

    static void markWinningPatternColumn(int currentSelection) {
        String temporaryString = "";
        for (int i = 0; i < LENGTH; i++) {
            if (i % 5 == currentSelection % 5) {
                temporaryString += "*";
            } else {
                temporaryString += BINGO_WINNING_PATTERNS.charAt(i);
            }
        }
        BINGO_WINNING_PATTERNS = temporaryString;
    }

    static String replace(String s, int index, String replacement) {
        return s.substring(0, index) + replacement + s.substring(index+1);
    }

    static void patternCreationTutorial () {
        System.out.println("TODO PATTERN MAKER TUT");
    }
}
