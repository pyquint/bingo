import java.io.IOException;
import java.util.Scanner;
import java.util.Random;

public class BINGO {
    // GLOBAL VARIABLES
    static Random RAND = new Random();
    static Scanner SCANNER = new Scanner(System.in);

    // " " AS SEPARATOR AND "!" AS FREE SPACE IN THE MIDDLE
    static final char SEPARATOR = 32, FREE_SPACE = SEPARATOR + 1;
    static final int ASCIIMIN = FREE_SPACE + 1, BINGOMAX = 75, MIDDLE = 12, LENGTH = 25;
    static final String TEMPLATE_PATTERN = "------------*------------";
    static String BINGO_WINNING_PATTERNS = "*---*-*-*---*---*-*-*---*" + SEPARATOR;
    static String ROLLED_NUMBERS_REPR = "", BINGO_CARD_REPRS = "", BINGO_CARD_PATTERNS = "", CURRENT_PATTERN = "";
    static String GRID_SEP = "\t";
    static int PATTERN_COUNT = 1, MONEY = 5, CARD_COUNT;

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

        /* ONE CARD AT THE START. THE PLAYER CAN EARN MONEY BY WINNING
         * PLAYERS CAN THEN BUY ADDITIONAL CARDS ???
         */

        // INITIALIZATION OF VARIABLES
        createBingoCardRepr();

        // TODO MONEYS
        // while (true) {
        //     System.out.println("(One card costs 2 MONEY. You currently have " + MONEY + "MONEY)");
        //     System.out.print("How many cards do you want?: " );
        //     CARD_NUM = SCANNER.nextInt();
        // }

        // PATTERN CREATION
        String response;
        do {
            System.out.print("Do you want to create a custom winning pattern? (y/n): ");
            response = SCANNER.nextLine().strip().toLowerCase();
        } while (!(response != "y" || response != "n"));

        int patternIndex = 0;
        do {
            patternIndex++;
            if (response.equals("y")) patternCreation(patternIndex);
            System.out.print("Create another pattern? (y/n): ");
            response = SCANNER.nextLine();
        } while (!response.equals("n"));

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

            randomNumber = getReprNumber(randomNumberRepr);
            membership = (BINGO_CARD_REPRS.contains(randomNumberRepr+"")) ? "May " : "Wala ";

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

            updateBingoCardPatterns();
            System.out.println(BINGO_CARD_REPRS);
            System.out.println(BINGO_CARD_PATTERNS);

            if (winningPatternsContainCardPatterns()) {
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

            ROLLED_NUMBERS_REPR += randomNumberRepr;
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
         * We start the mapping at the 34rd character ('"') up to 108 ('l', 33 + 75 - 1 because of indexing).
         * The SPACE is reserved as the separator between player cards.
         */
        char randNumberRepr;
        int min, max;
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5; col++) {
                 if (row == 2 && col == 2) {
                    BINGO_CARD_REPRS += FREE_SPACE;
                    continue;
                }
                min = col * 15 + 1;
                max = (col + 1) * 15 + 1;
                do {
                    randNumberRepr = getNumberRepr(getRandomNumber(min, max));
                } while (BINGO_CARD_REPRS.contains(randNumberRepr+""));
                BINGO_CARD_REPRS += randNumberRepr+"";
            }
        }
        BINGO_CARD_PATTERNS += SEPARATOR;
    }

    static void printBingoCardRepr() {
        /*
         * Since bingoCardRepr is a 1-dimentional form, there is no need for nested loops.
         * I instead used conditionals to further control the printing of the card.
         */
        char currentNumRepr;
        int currentNum;

        System.out.println("B" + GRID_SEP + "I" + GRID_SEP + "N" + GRID_SEP + "G" + GRID_SEP + "O");

        for (int i = 0; i < LENGTH; i++) {
            currentNumRepr = BINGO_CARD_REPRS.charAt(i);

            if (currentNumRepr == FREE_SPACE) {
                System.out.print("FS" + GRID_SEP);
            } else {
                currentNum = getReprNumber(currentNumRepr);
                // enclose the number with parentheses if the number is already called out, else print as it is
                System.out.print(ROLLED_NUMBERS_REPR.contains(currentNumRepr+"") ? "(" + currentNum + ")" : currentNum);
                System.out.print(GRID_SEP);
            }
            if (i % 5 == 4) {
                System.out.print('\n');
            }
        }
    }

    static void patternCreation(int patternIndex) throws IOException, InterruptedException {
        /*
         * A `pattern` is a string of '*'s and '-', where '*' is a marked square, while '-' is the default.
         * All winning patterns, are stored in a single `bingoWinningPatterns` String, separated by  ',' between.
         *
         * Player bingo cards are also converted into patterns, and we can simply iterate the cardPattern against the winningPatterns if the marks are matching.
         */

        // TOOL TUTORIAL
        patternCreationTutorial();

        String action;
        boolean inTool = true;
        int currentSelection = 0;
        CURRENT_PATTERN = TEMPLATE_PATTERN;

        while (inTool) {
            cls();
            // PRINTING THE CURRENT PATTERN MAKER CARD
            System.out.println("PATTERN MAKER TOOL\n");
            System.out.println("B" + GRID_SEP + "I" + GRID_SEP + "N" + GRID_SEP + "G" + GRID_SEP + "O");
            for (int j = 0; j < LENGTH; j++) {
                if (j == currentSelection) {
                    System.out.print("[" + CURRENT_PATTERN.charAt(j) + "]");
                } else {
                    System.out.print(CURRENT_PATTERN.charAt(j));
                }

                if (j % 5 == 4) {
                    System.out.println('\n');
                } else {
                    System.out.print(GRID_SEP);
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
                System.out.print("Action: ");
                action = SCANNER.nextLine().toLowerCase().strip();

                if (action.equals("q")) {
                    CURRENT_PATTERN = replace(CURRENT_PATTERN, currentSelection, "*");
                } else if (action.equals("z")) {
                    markCurrentPatternRow(currentSelection);
                } else if (action.equals("x")) {
                    markCurrentPatternColumn(currentSelection);
                } else if (action.equals("e")) {
                    System.out.println("Exiting pattern tool...");
                    inTool = false;
                } else if (action.equals("r")) {
                    resetCurrentTemplatePattern();
                } else if (action.equals("w") && currentSelection > 4) {
                    currentSelection -= 5;
                } else if (action.equals("a") && currentSelection % 5 > 0) {
                    currentSelection -= 1;
                } else if (action.equals("s") && currentSelection < 20) {
                    currentSelection += 5;
                } else if (action.equals("d") && currentSelection % 5 < 4) {
                    currentSelection += 1;
                } else {
                    System.out.println("boop");
                }
                break;
            }
        }
        BINGO_WINNING_PATTERNS += CURRENT_PATTERN + SEPARATOR;
        PATTERN_COUNT++;
    }

    static String getTemplatePattern() {
        String template = "";
        for (int i = 0; i < LENGTH; i++) {
            template += ((i == MIDDLE) ? "*" : "-");
        }
        return template;
    }

    static void resetCurrentTemplatePattern() {
        CURRENT_PATTERN = TEMPLATE_PATTERN;
    }

    static void updateBingoCardPatterns() {
        BINGO_CARD_PATTERNS = "";
        for (int i = 0; i < BINGO_CARD_REPRS.length(); i++) {
            char currentChar = BINGO_CARD_REPRS.charAt(i);
            if (currentChar == FREE_SPACE) BINGO_CARD_PATTERNS += "*";
            BINGO_CARD_PATTERNS += (ROLLED_NUMBERS_REPR.contains(currentChar+"")) ? '*' : '-';
        }
    }

    static boolean winningPatternsContainCardPatterns() {
        // somehow skip checking if there is no pattern (returns true because of free space spot)
        // maybe add default pattern or don't start the game if there is no pattern
        boolean won = true;
        for (int i = (PATTERN_COUNT - 1) * 25 + PATTERN_COUNT; i < PATTERN_COUNT + 1; i++) {
            for (int j = 0; j < LENGTH; j++) {
                char currCard = BINGO_CARD_PATTERNS.charAt(j);
                char currPatt = BINGO_WINNING_PATTERNS.charAt(j);
                if (currPatt == '*' && currCard != '*') won = false;
            }
            if (won) return won;
        }
        return won;
    }

    static void markCurrentPatternRow(int currentSelection) {
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
        CURRENT_PATTERN = CURRENT_PATTERN.substring(0, leftMostIndex) + "*****" + CURRENT_PATTERN.substring(leftMostIndex+5);
    }

    static void markCurrentPatternColumn(int currentSelection) {
        String temporaryString = "";
        for (int i = 0; i < LENGTH; i++) {
            if (i % 5 == currentSelection % 5) {
                temporaryString += "*";
            } else {
                temporaryString += CURRENT_PATTERN.charAt(i);
            }
        }
        CURRENT_PATTERN = temporaryString;
    }

    static String replace(String s, int index, String replacement) {
        return s.substring(0, index) + replacement + s.substring(index+1);
    }

    static void patternCreationTutorial () {
        // Queenielyn
        System.out.println("TODO PATTERN MAKER TUT");
    }

    static void playTutorial() throws IOException, InterruptedException {
        // Queenielyn
        cls();
        printlnInteractive("Hello! Welcome to the BINGO tutorial!");
        printInteractive("Let's start with the mechanics.");
        printlnInteractive("BINGO is a game of chance.");
        printlnInteractive("Once every turn, a number is rolled randomly.");
        printlnInteractive("""
                On a BINGO card that looks something like this:
                12\t22\t43\t51\t72\n
                5\t23\t35\t57\t61\n
                9\t28\tB\t48\t69\n
                11t29\t41\t49\t62\n
                6\t19\t42\t50\t65\n
                if the rolled number is present, that square gets marked.""");
        printlnInteractive("""
                The middle, denoted B, is a sort of free square.
                Consider it already marked.""");
        printlnInteractive("""
                The game is won if five (5) marks in a row is achieved,
                either vertically, horizontally, or diagonally.""");
        // TODO
        printlnInteractive("Good luck and have fun!");
        cls();
    }

    static int getRandomNumber(int min, int max) {
        // .nextInt((max-min) + min) --> range between min (inclusive) and max (exclusive)
        return RAND.nextInt(max - min) + min;
    }

    static char getNumberRepr(int number) {
        return (char) (number + ASCIIMIN);
    }

    static int getReprNumber(char repr) {
        return repr - ASCIIMIN;
    }

    static void printlnInteractive(String s) {
        System.out.println(s + " (ENTER) ");
        SCANNER.nextLine();
    }

    static void printInteractive(String s) {
        System.out.print(s + " (ENTER) ");
        SCANNER.nextLine();
    }

    static void cls() throws IOException, InterruptedException {
        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
    }
}
