import java.io.IOException;
import java.util.Scanner;
import java.util.Random;

public class BINGO {
    // BINGO by us.
    /*
     * We restricted ourselves to only using the discussed programming constructs in
     * the majority of the main logic, except for Random which we were allowed to
     * use. We have introduced some higher-level techniques, although they are
     * purely for aesthetic and code maintainability purposes only (functions,
     * clearing of the terminal, Thread sleep printing, etc.).
     *
     * While you may want to gouge your eyes after looking at the code, I hope you
     * understand what with the deficiency in knowledge of the programmers, and
     * limitations in compliance with the requirements of the project.
     *
     * Good day and have fun playing BINGO!
     */

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

    // keys for mark/unmark, mark column, mark row, reset pattern, save and exit
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
    static int PATTERN_COUNT;

    static String USERNAME;
    static String USER_CARDS_REPR;
    static String USER_CARD_PATTERNS_REPR;
    static int USER_CARD_COUNT;

    static String COMP_CARDS_REPR;
    static String COMP_CARD_PATTERNS_REPR;
    static int COMP_CARD_COUNT;

    static double USER_MONEY;
    static double COMP_MONEY;
    static final double CARD_COST = 5;
    static final double PRIZE_PER_WIN = 20;
    static final double STARTING_MONEY = 25;

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
        do {
            letsPlayBingo();
        } while (!isYesWhenPrompted("Exit the program?"));
        System.out.println(BINGOSHAKE + "\n");
        System.out.println("This was BINGO! Goodbye!");
    }

    static void letsPlayBingo() throws IOException, InterruptedException {
        // WELCOME SCREEN
        System.out.println(BINGOSHAKE);
        System.out.println(BINGOASCII);

        int gameCount = 1;
        USER_MONEY = COMP_MONEY = STARTING_MONEY;

        // NAME CREATION
        while (true) {
            System.out.print("\nWhat would you like to name yourself?: ");
            USERNAME = SCANNER.nextLine();
            if (USERNAME.equalsIgnoreCase("computer")) {
                System.out.println("You cannot name yourself as '" + USERNAME + "'!");
                System.out.println("Please use another name!\n");
                continue;
            } else if (USERNAME.isBlank() || USERNAME.isEmpty()) {
                System.out.println("Please include characters!");
                continue;
            }
            if (isYesWhenPrompted("Is '" + USERNAME + "' final?"))
                break;
        }

        System.out.println("\nWELCOME, " + USERNAME + "!");

        do {
            // INITIALIZATION OF VARIABLES
            USER_CARDS_REPR = "";
            USER_CARD_PATTERNS_REPR = "";
            COMP_CARDS_REPR = "";
            COMP_CARD_PATTERNS_REPR = "";
            WINNING_PATTERNS_REPR = "";
            ROLLED_NUMBERS_REPR = "";
            PATTERN_COUNT = 0;

            // HELP MODULE

            // TOOL TUTORIAL
            // patternCreationTutorial();

            // MONETARY SYSTEM
            if (USER_MONEY < CARD_COST) {
                cls();
                printlnInteractive("\nGAME OVER! You don't have enough money to buy more cards!");
                System.out.println();
                break;
            } else if (COMP_MONEY < CARD_COST) {
                cls();
                printlnInteractive("\nYOU WIN! Computer can't afford to buy any more cards now!!");
                System.out.println();
                break;
            }

            if (gameCount == 1) {
                if (isYesWhenPrompted("\nDo you want to go to the turorial first?"))
                    playTutorial();
                System.out.println("\nYou start with P" + STARTING_MONEY
                        + ", but you get to have one card for free in your first game!");
                USER_CARD_COUNT = COMP_CARD_COUNT = 1;
            } else {
                cls();
                System.out.println("1 Card -> P" + CARD_COST);
                System.out.println("Current money: P" + USER_MONEY);
                System.out.println("Computer's money: P" + COMP_MONEY + "\n");
                while (true) {
                    System.out.print("How many cards do you want to buy?: ");
                    USER_CARD_COUNT = SCANNER.nextInt();
                    SCANNER.nextLine();
                    if (USER_CARD_COUNT * CARD_COST <= USER_MONEY)
                        break;
                    System.out.println("Insufficient money!\n");
                }
                USER_MONEY -= USER_CARD_COUNT * CARD_COST;
                System.out.println("\nYou bought " + USER_CARD_COUNT + " card" + ((USER_CARD_COUNT > 1) ? "s. " : ". ")
                        + "Remaining money: P" + USER_MONEY);

                do {
                    COMP_CARD_COUNT = getRandomNumber(1, USER_CARD_COUNT + 3);
                } while (COMP_CARD_COUNT * CARD_COST >= COMP_MONEY);
                COMP_MONEY -= COMP_CARD_COUNT * CARD_COST;
                System.out.println("Computer bought " + COMP_CARD_COUNT + ", with P" + COMP_MONEY + " on its pockets.");
            }

            createBingoCardsRepr(COMP_CARD_COUNT, "computer");
            createBingoCardsRepr(USER_CARD_COUNT, USERNAME);

            // PATTERN CREATION
            boolean isCreatingPattern = isYesWhenPrompted("\nDo you want to create and use a custom winning pattern?");
            while (isCreatingPattern) {
                patternCreation();
                isCreatingPattern = isYesWhenPrompted("Create another pattern?");
                if (!isCreatingPattern) {
                    cls();
                    System.out.println("Exited Pattern Maker Tool...");
                }
            }

            if (PATTERN_COUNT == 0) {
                System.out.println("\nDEFAULT WINNING PATTERNS: X, VERTICAL,HORIZONTAL, AND BLACKOUT");
                WINNING_PATTERNS_REPR += convertPattToInt("*---*-*-*---*---*-*-*---*") + SEPSTR;
                WINNING_PATTERNS_REPR += convertPattToInt("*************************") + SEPSTR;
                WINNING_PATTERNS_REPR += convertPattToInt("*----*----*---*----*----") + SEPSTR;
                WINNING_PATTERNS_REPR += convertPattToInt("-*----*----*----*----*---") + SEPSTR;

                PATTERN_COUNT = 4;
            }

            // MAIN GAME LOOP
            printlnInteractive("\nWe're all set! Tara BINGO!");
            bingoGameLoop();
            gameCount++;

        } while (isYesWhenPrompted("Do you want to play again ?"));
    }

    static void bingoGameLoop() throws IOException, InterruptedException {
        char randomNumberRepr;
        int randomNumber;
        int winningCardNo;
        String membership;
        String checkedPlayer;
        int playerCheckingCounter;

        gameLoop: while (true) {
            cls();

            System.out.println(USERNAME + "'S CARD/S:");
            printCardsUpdatePatterns(USERNAME);
            System.out.println("\nCOMPUTER'S CARD/S:");
            printCardsUpdatePatterns("computer");

            // Randomize in a 50-50 chance who to check first, which in turn is the one to
            // be called winner should they have the winning pattern.
            playerCheckingCounter = getRandomNumber(0, 2);

            for (int i = 0; i < 2; i++) {
                checkedPlayer = playerCheckingCounter == 1 ? USERNAME : "computer";
                winningCardNo = cardContainsWinningPattern(checkedPlayer);

                if (winningCardNo != -1) {
                    System.out.println(BINGOASCII);
                    System.out.println(checkedPlayer + " WINS!");
                    printlnInteractive("WINNING CARD: No." + winningCardNo + "!");

                    if (checkedPlayer.equals("computer")) {
                        COMP_MONEY += PRIZE_PER_WIN;
                    } else {
                        USER_MONEY += PRIZE_PER_WIN;
                    }

                    System.out.println(checkedPlayer + " wins P" + PRIZE_PER_WIN + ".\n");
                    break gameLoop;
                }
                playerCheckingCounter = (playerCheckingCounter + 1) % 2;
            }

            System.out.println("Taya taya...");
            System.out.print("Sa letra sang");

            // for (int i = 0; i < getRandomNumber(2, 5); i++) {
            // System.out.print(".");
            // Thread.sleep(getRandomNumber(100, 501));
            // }

            do {
                randomNumberRepr = getNumberRepr(getRandomNumber(1, BINGOMAX + 1));
            } while (ROLLED_NUMBERS_REPR.indexOf(randomNumberRepr) != -1);

            ROLLED_NUMBERS_REPR += randomNumberRepr;
            randomNumber = getReprNumber(randomNumberRepr);

            System.out.print(" " + BINGO.charAt((randomNumber - (randomNumber % 16)) / 15) + "! ");

            // for (int i = 0; i < getRandomNumber(3, 7); i++) {
            // System.out.print(".");
            // Thread.sleep(getRandomNumber(100, 251));
            // }

            // Thread.sleep(getRandomNumber(250, 501));
            // printInteractive(" " + randomNumber + "!");

            membership = (USER_CARDS_REPR.indexOf(randomNumberRepr) != -1) ? "May ara" : "Wala";
            System.out.println("\n" + membership + " ka " + randomNumber + "!");

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
         * character, and vice versa. We start the mapping at the 34th character ('"')
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
        boolean marked;
        int currentNum;
        char currentNumRepr;
        int patternBits;
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
         */

        // @formatter:off
        // * This is what the indices would look like:
        // * 24 23 22 21 20
        // * 19 18 17 16 15
        // * 14 13 12 11 10
        // * 09 08 07 06 05
        // * 04 03 02 01 00
        // @formatter:on

        char currChar;
        int leftIndex, topIndex;

        int bits = 0;
        int currentSelection = MSBIndex;
        boolean isInTool = true;

        String action;

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
                11\t29\t41\t49\t62\n
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
