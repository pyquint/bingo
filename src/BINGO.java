import java.io.IOException;
import java.util.Scanner;
import java.util.Random;

public class BINGO {
    // GLOBAL VARIABLES
    static Random RAND = new Random();
    static Scanner SCANNER = new Scanner(System.in);
    static ProcessBuilder cmdProcess = new ProcessBuilder("cmd", "/c", "cls").inheritIO();

    // " " (32 in ASCII) AS SEPARATOR FOR PATTERN AND "!" AS FREE SPACE
    static final char SEPCHAR = 32;
    static final char FREE_SPACE = SEPCHAR + 1;
    static final String SEPSTR = SEPCHAR + "";
    static final String GRID_SEP = "\t";

    static final int ASCIIMIN = FREE_SPACE + 1;
    static final int BINGOMAX = 75;
    static final int LENGTH = 25;
    static final int MIDDLE = 12;
    static final int MSBIndex = LENGTH - 1;

    static final String markUnmark = "q", markCol = "z", markRow = "x", reset = "r", wq = "e";

    /*
     * There is NO SEPARATOR between card reprs, only in pattern reprs, since we can
     * control the printing of card repr, and there is no need to print patterns
     * (for now).
     *
     * if using substrings: Substring of each reprs and sequences is [start, start +
     * LENGTH], where start = i * LENGTH. However, if you decided to add separation,
     * use start = i * LENGTH + (i * 1).
     */

    static String WINNING_PATTERNS_REPR;
    static String ROLLED_NUMBERS_REPR;

    static String USERNAME;
    static String USER_CARDS_REPR;
    static String USER_CARD_PATTERNS_REPR;
    static int USER_CARD_COUNT;

    static String COMP_CARDS_REPR;
    static String COMP_CARD_PATTERNS_REPR;
    static int COMP_CARD_COUNT;

    static int PATTERN_COUNT;
    static int USER_MONEY = 0;
    static int COMP_MONEY = 0;

    static String BINGO = "BINGO";
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
    static String BINGOSHAKE = """
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
        // boolean isFirstGame = true;

        while (true) {
            System.out.print("\nWhat would you like to name yourself?: ");
            USERNAME = SCANNER.nextLine();
            if (USERNAME.toLowerCase().equals("computer")) {
                System.out.println("You cannot name yourself as '" + USERNAME + "'!");
                System.out.println("Please use another name!\n");
                continue;
            }
            if (isYesWhenPrompted("Is '" + USERNAME + "' final?"))
                break;
        }

        System.out.println("\nWELCOME, " + USERNAME + "!");

        do {
            /*
             * ONE CARD AT THE START. THE PLAYER CAN EARN MONEY BY WINNING PLAYERS CAN BUY
             * ADDITIONAL CARDS ???
             */

            // INITIALIZATION OF VARIABLES
            USER_CARDS_REPR = "";
            USER_CARD_PATTERNS_REPR = "";
            COMP_CARDS_REPR = "";
            COMP_CARD_PATTERNS_REPR = "";
            WINNING_PATTERNS_REPR = "";
            ROLLED_NUMBERS_REPR = "";
            PATTERN_COUNT = 0;
            USER_MONEY = 0;

            // TOOL TUTORIAL
            // if (isYesWhenPrompted("Do you want to go to the turorial first?"))
            // playTutorial();

            // HELP MODULE
            // patternCreationTutorial();

            System.out.print("\nHow many cards do you want to hold? ");
            USER_CARD_COUNT = SCANNER.nextInt();
            createBingoCardsRepr(USER_CARD_COUNT, USERNAME);

            COMP_CARD_COUNT = getRandomNumber(USER_CARD_COUNT, USER_CARD_COUNT + 2);
            createBingoCardsRepr(COMP_CARD_COUNT, "computer");

            SCANNER.nextLine();

            // PATTERN CREATION
            boolean isCreatingPattern = isYesWhenPrompted("Are you the game host? Create a custom winning pattern?");
            while (isCreatingPattern) {
                patternCreation();
                isCreatingPattern = isYesWhenPrompted("Create another pattern?");
                if (!isCreatingPattern) {
                    System.out.println("Exiting tool...");
                }
            }

            if (!isCreatingPattern && PATTERN_COUNT == 0) {
                printInteractive("\nDEFAULT WINNING PATTERNS: X and BLACKOUT");
                WINNING_PATTERNS_REPR += convertPattToInt("*---*-*-*---*---*-*-*---*") + SEPSTR;
                WINNING_PATTERNS_REPR += convertPattToInt("*************************") + SEPSTR;
                PATTERN_COUNT += 2;
            }

            // MAIN GAME LOOP
            letsPlayBingo();
        } while (isYesWhenPrompted("Play again?"));

        System.out.println("Thank you for playing!");
    }

    static void letsPlayBingo() throws IOException, InterruptedException {
        char randomNumberRepr;
        int randomNumber;
        int winningCardNo;
        String membership;
        String playerChecking;
        int playerCheckingCounter;

        inGame: while (true) {
            cls();

            System.out.println(USERNAME + "'S CARDS:");
            printCardsUpdatePatterns(USERNAME);
            System.out.println("\nCOMPUTER'S CARDS:");
            printCardsUpdatePatterns("computer");

            // Randomize in a 50-50 chance who to check first, which in turn is the one to
            // be called winner should they have the winning pattern.

            playerCheckingCounter = getRandomNumber(0, 2);
            for (int i = 0; i < 2; i++) {
                playerChecking = playerCheckingCounter == 1 ? USERNAME : "computer";
                winningCardNo = cardContainsWinningPattern(playerChecking);
                if (winningCardNo != -1) {
                    System.out.println(BINGOASCII);
                    System.out.println(playerChecking + " WINS!");
                    printlnInteractive("WINNING CARD: No." + winningCardNo + "!");
                    cls();
                    break inGame;
                }
                playerCheckingCounter = (playerCheckingCounter + 1) % 2;
            }

            // System.out.print(BingoShake);
            System.out.println("Taya taya...");
            System.out.print("Sa letra sang");

            // TODO remove when submitting.
            // for (int i = 0; i < getRandomNumber(3, 7); i++) {
            // System.out.print(".");
            // Thread.sleep(getRandomNumber(250, 501));
            // }

            do {
                randomNumberRepr = getNumberRepr(getRandomNumber(1, BINGOMAX + 1));
            } while (ROLLED_NUMBERS_REPR.indexOf(randomNumberRepr) != -1);

            ROLLED_NUMBERS_REPR += randomNumberRepr;
            randomNumber = getReprNumber(randomNumberRepr);

            System.out.print(BINGO.charAt((randomNumber - (randomNumber % 16)) / 15));

            // for (int i = 0; i < getRandomNumber(2, 5); i++) {
            // System.out.print(".");
            // Thread.sleep(getRandomNumber(100, 401));
            // }

            // Thread.sleep(getRandomNumber(500, 1001));
            // printInteractive(randomNumber + "!");

            membership = (USER_CARDS_REPR.indexOf(randomNumberRepr) != -1) ? "May " : "Wala ";
            System.out.println("\n" + membership + randomNumber + " ka!");

            // System.out.print("Ano nga card may " + randomNumber + "? ");
            // memberships = SCANNER.nextLine();

            // System.out.println();
            // for (int i = 1; i < 11; i++) {
            // System.out.print(i);
            // Thread.sleep(1000);
            // System.out.print("\r");
            // }

            // printInteractive("\nRoll again >>>");
        }

    }

    static void createBingoCardsRepr(int cardCount, String player) {
        /*
         * bingoCardRepr is a one-directional String composed of 25 * CARD_COUNT
         * characters. We cannot naively store the card numbers as numerical values, or
         * verify membership of single-digit numbers. For example: We rolled a '1'. If
         * we use bingoCardRepr.contains('1'), it will look for 1, 12, 13, ..., 21, 31,
         * and so on.
         *
         * One technique I could think of is we could change the way we check the
         * membership of rolled numbers. But as of right now I cannot think of anything
         * about this.
         *
         * What I came up with and ultimately chose was (Repr)esenting the rolled number
         * into an ASCII character. The number is always converted to its associated
         * character, and vice versa. We start the mapping at the 34rd character ('"')
         * up to 108 ('l', 33 + 75 - 1 because of indexing).
         */

        String card;
        char randNumberRepr;
        int min, max;

        for (int i = 0; i < cardCount; i++) {
            card = "";
            for (int row = 0; row < 5; row++) {
                for (int col = 0; col < 5; col++) {
                    if (row == 2 && col == 2) {
                        card += FREE_SPACE;
                        continue;
                    }
                    min = col * 15 + 1;
                    max = (col + 1) * 15 + 1;
                    do {
                        randNumberRepr = getNumberRepr(getRandomNumber(min, max));
                    } while (card.indexOf(randNumberRepr) != -1);
                    card += randNumberRepr;
                }
            }

            if (player.equals("computer")) {
                COMP_CARDS_REPR += card;
            } else {
                USER_CARDS_REPR += card;
            }
        }
    }

    static void printCardsUpdatePatterns(String player) {
        int patternBits;
        boolean marked;
        int currentNum;
        char currentNumRepr;

        int count;
        String card_repr;

        if (player.equals("computer")) {
            COMP_CARD_PATTERNS_REPR = "";
            card_repr = COMP_CARDS_REPR;
            count = COMP_CARD_COUNT;
        } else {
            USER_CARD_PATTERNS_REPR = "";
            card_repr = USER_CARDS_REPR;
            count = USER_CARD_COUNT;
        }

        // Use conditionals to further control the printing of the card.
        for (int i = 0; i < count; i++) {
            patternBits = 0;

            System.out.println("Card no. " + (i + 1));
            System.out.println("B" + GRID_SEP + "I" + GRID_SEP + "N" + GRID_SEP + "G" + GRID_SEP + "O");

            for (int j = 0; j < LENGTH; j++) {
                marked = true;
                currentNumRepr = card_repr.charAt(j + (i * LENGTH));
                currentNum = getReprNumber(currentNumRepr);

                if (ROLLED_NUMBERS_REPR.indexOf(currentNumRepr) != -1) {
                    System.out.print("(" + currentNum + ")");
                } else if (currentNumRepr == FREE_SPACE) {
                    System.out.print("FS");
                } else {
                    System.out.print(currentNum);
                    marked = false;
                }

                if (marked)
                    patternBits = patternBits | 1 << (LENGTH - j - 1);

                if (j % 5 == 4) {
                    System.out.print('\n');
                } else {
                    System.out.print(GRID_SEP);
                }
            }

            if (player.equals("computer")) {
                COMP_CARD_PATTERNS_REPR += patternBits + SEPSTR;
            } else {
                USER_CARD_PATTERNS_REPR += patternBits + SEPSTR;
            }
            System.out.println();
        }
    }

    static void patternCreation() throws IOException, InterruptedException {
        /*
         * A `pattern` is composed of marked squares in a player card and a winning
         * pattern. It is (Repr)esented/stored as a String of the number equivalent to
         * that pattern's squares when converted into a series of bits, where a marked
         * square is 1 and an unmarked one as 0 (this number is an int (2^32), enough
         * space for a 25-bit max value).
         *
         * Checking for a matching pattern in the card involves converting that String
         * number into an int and perform bitwise AND (see the pattern checking function
         * for actual implementation).
         *
         * We start from the 25th bit (24th index) of the pattern integer and printing
         * of loops. The index being one less than current selection works well in bit
         * shifting. If we want to flip the bit at index i, we OR the bits with 1
         * shifted by i. e.g. 01 | 1 << 1 (index 1) --> 01 | 10 --> 11
         *
         * 24 23 22 21 20 19 18 17 16 15 14 13 12 11 10 9 8 7 6 5 4 3 2 1 0
         */

        char currChar;
        int leftIndex, topIndex;

        int bits = 0;
        int currentSelection = MSBIndex;
        boolean isInTool = true;

        String action;
        // keys for mark/unmark, mark column, mark row, reset pattern, save and exit

        while (isInTool) {
            cls();
            System.out.println("PATTERN MAKER TOOL\n");
            System.out.println("B" + GRID_SEP + "I" + GRID_SEP + "N" + GRID_SEP + "G" + GRID_SEP + "O");

            // PRINTING THE CURRENT PATTERN MAKER CARD
            for (int i = MSBIndex; i >= 0; i--) {
                // if bit AND'd with ith shift is not 0, the current character is marked
                currChar = ((bits & 1 << i) != 0) ? '*' : '-';

                if (i == currentSelection) {
                    System.out.print("[" + currChar + "]");
                } else if (i == MIDDLE) {
                    System.out.print('*');
                } else {
                    System.out.print(currChar);
                }

                if (i % 5 == 0) {
                    System.out.println('\n');
                } else {
                    System.out.print(GRID_SEP);
                }
            }
            System.out.println();

            System.out.println("[wasd] Move current selection");
            System.out.println("[" + markUnmark + "] Mark/unmark current selection");
            System.out.println("[" + markRow + "] Mark whole row");
            System.out.println("[" + markCol + "] Mark whole column");
            // System.out.println("[n] Discard and exit");
            System.out.println("[" + reset + "] Reset");
            System.out.println("[" + wq + "] Done/Exit");

            while (true) {
                System.out.print("\nAction: ");
                action = SCANNER.nextLine().toLowerCase().strip();

                if (action.equals(markUnmark)) {
                    // mark/unmark current selection
                    bits = bits ^ 1 << currentSelection;
                } else if (action.equals("w") && currentSelection < 20) {
                    currentSelection += 5;
                } else if (action.equals("a") && currentSelection % 5 < 4) {
                    currentSelection += 1;
                } else if (action.equals("s") && currentSelection > 4) {
                    currentSelection -= 5;
                } else if (action.equals("d") && currentSelection % 5 > 0) {
                    currentSelection -= 1;
                } else if (action.equals(reset)) {
                    bits = 0;
                    currentSelection = MSBIndex;
                } else if (action.equals(wq)) {
                    System.out.println();
                    isInTool = false;
                } else if (action.equals(markRow)) {
                    // leftIndex is the index of the current row's leftmost square
                    // (actually calculates the rightmost since we iterate backwards)
                    leftIndex = currentSelection - (currentSelection % 5);
                    for (int j = 0; j < 5; j++) {
                        bits = bits | 1 << leftIndex + j;
                    }
                } else if (action.equals(markCol)) {
                    // topIndex is the index of the current column's topmost square
                    // (is not reversed since up is up regardless of direction)
                    topIndex = currentSelection % 5;
                    for (int k = 0; k < 5; k++) {
                        bits = bits | 1 << topIndex + k * 5;
                    }
                } else {
                    System.out.println("Invalid input.");
                    continue;
                }
                break;
            }
        }

        if (bits > 0) {
            WINNING_PATTERNS_REPR += bits + SEPSTR;
            PATTERN_COUNT++;
        }
    }

    static int cardContainsWinningPattern(String player) {
        String cardPatternsRepr;
        int cardCount;
        if (player.equals("computer")) {
            cardPatternsRepr = COMP_CARD_PATTERNS_REPR;
            cardCount = COMP_CARD_COUNT;
        } else {
            cardPatternsRepr = USER_CARD_PATTERNS_REPR;
            cardCount = USER_CARD_COUNT;
        }

        int winningPatternBits, cardPatternBits;
        int nextWinningPatternIndex = 0, nextCardPatternIndex = 0;
        String winningPatternRepr, patternRepr;
        char currentWinningPatternChar, currentCardPatternChar;

        for (int patternCount = 0; patternCount < PATTERN_COUNT; patternCount++) {
            winningPatternRepr = "";
            nextCardPatternIndex = 0;

            for (int i = 0; i < WINNING_PATTERNS_REPR.length(); i++) {
                currentWinningPatternChar = WINNING_PATTERNS_REPR.charAt(i + nextWinningPatternIndex);
                if (currentWinningPatternChar == SEPCHAR)
                    break;
                winningPatternRepr += currentWinningPatternChar;
            }
            nextWinningPatternIndex += winningPatternRepr.length() + 1;
            winningPatternBits = Integer.parseInt(winningPatternRepr);

            for (int count = 0; count < cardCount; count++) {
                patternRepr = "";

                for (int j = 0; j < cardPatternsRepr.length(); j++) {
                    currentCardPatternChar = cardPatternsRepr.charAt(j + nextCardPatternIndex);
                    if (currentCardPatternChar == SEPCHAR)
                        break;
                    patternRepr += currentCardPatternChar;
                }
                nextCardPatternIndex += patternRepr.length() + 1;
                cardPatternBits = Integer.parseInt(patternRepr);

                if ((winningPatternBits & cardPatternBits) == winningPatternBits)
                    return count + 1;
            }

        }
        return -1;
    }

    static int convertPattToInt(String pattern) {
        // An artifact of a bygone era, but preserved for debugging purposes and
        // creating default pattern reprs. Might have more use in the future.
        // The `pattern` parameter is a String composed of '*' and '-'.
        int bits = 0;
        for (int i = 0; i < LENGTH; i++) {
            if (pattern.charAt(i) == '*')
                bits = bits | (1 << LENGTH - 1 - i);
        }
        return bits;
    }

    static void patternCreationTutorial() {
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

    static boolean isYesWhenPrompted(String prompt) {
        String response;
        do {
            System.out.print(prompt + " (y/n): ");
            response = SCANNER.nextLine().strip().toLowerCase();
        } while (!(response.equals("y") || response.equals("n")));
        return response.equals("y");
    }

    static int getRandomNumber(int min, int max) {
        // .nextInt((max-min) + min) --> range min (inclusive) and max (exclusive)
        return RAND.nextInt(max - min) + min;
    }

    static char getNumberRepr(int number) {
        return (char) (number + ASCIIMIN);
    }

    static int getReprNumber(char repr) {
        return repr - ASCIIMIN;
    }

    static void printlnInteractive(String s) {
        System.out.print(s + " (ENTER) ");
        SCANNER.nextLine();
    }

    static void printInteractive(String s) {
        System.out.print(s + " (ENTER) ");
        SCANNER.nextLine();
    }

    static void cls() throws IOException, InterruptedException {
        cmdProcess.start().waitFor();
    }
}
