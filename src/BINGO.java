import java.io.IOException;
import java.util.Scanner;
import java.util.Random;

public class BINGO {
    // BINGO by us.
    // Best played in a vertical terminal.

    /*
     * We limited ourselves with discussed programming constructs
     * and the allowed Random module for as much as possible.
     * But alas have utilized some higher-level techniques, for
     * aesthetic, and code reusability, and maintainability purposes
     * (functions, clearing of the terminal, Thread sleep printing, etc.).
     *
     * While you may want to gouge your eyes after looking at the code, do know
     * the deficiency and limitations in the knowledge of the programmers and
     * goal to be in compliance with the requirements of the project.
     *
     * Good day and have fun playing BINGO!
     */

    // GLOBAL VARIABLES

    // " " (32 in ASCII) AS SEPARATOR FOR PATTERN AND "!" AS FREE SPACE IN REPR
    static final char SEPARATOR_CHAR = 32;
    static final char FREE_SPACE = SEPARATOR_CHAR + 1;
    static final int ASCII_MIN = FREE_SPACE + 1;
    static final String SEPARATOR_STRING = " ";
    static final String GRID_SEP = "\t";
    static final int BINGO_MAX = 75;
    static final int LENGTH = 25;
    static final int MIDDLE = 12;
    static final int MSB_INDEX = LENGTH - 1;
    static final double CARD_COST = 5;
    static final double PRIZE_PER_WIN = 20;
    static final double STARTING_MONEY = 25;
    // keybindings for the pattern maker tool
    static final String markUnmarkSqr = "q";
    static final String markUnmarkCol = "z";
    static final String markUnmarkRow = "x";
    static final String resetPattern = "r";
    static final String useCustomPatt = "t";
    static final String exitPattTool = "e";
    static final String DEFAULT_WINNING_PATTERNS_REPR = (
            convertPattToInt("*---*-*-*---*---*-*-*---*") + SEPARATOR_STRING +
                    convertPattToInt("*************************") + SEPARATOR_STRING +
                    convertPattToInt("*----*----*----*----*----") + SEPARATOR_STRING + // Vertical 1
                    convertPattToInt("-*----*----*----*----*---") + SEPARATOR_STRING +// vrow 2
                    convertPattToInt("--*----*----*----*----*--") + SEPARATOR_STRING +// vRow 3
                    convertPattToInt("---*----*----*----*----*-") + SEPARATOR_STRING + // vRow 4
                    convertPattToInt("----*----*----*----*----*") + SEPARATOR_STRING +// vRow 5
                    convertPattToInt("*****--------------------") + SEPARATOR_STRING +// Horizontal 1
                    convertPattToInt("-----*****---------------") + SEPARATOR_STRING +// hRow 2
                    convertPattToInt("----------*****----------") + SEPARATOR_STRING +// hRow 3
                    convertPattToInt("---------------*****-----") + SEPARATOR_STRING +// hRow 4
                    convertPattToInt("--------------------*****") + SEPARATOR_STRING); // hRow 5


    /*
     * There is NO SEPARATOR between card reprs, only in pattern reprs, since we can
     * control the printing of card repr, and there is no need to print patterns
     * (for now).
     *
     * if using substrings: Substring of each reprs and sequences is [start, start +
     * LENGTH], where start = i * LENGTH. However, if you decided to add separation,
     * use start = i * LENGTH + (i * 1).
     */
    static final String COMP_NAME = "COMP";
    static Random RAND = new Random();
    static Scanner SCANNER = new Scanner(System.in);
    static ProcessBuilder cmdProcess = new ProcessBuilder("cmd", "/c", "cls").inheritIO();
    static String WINNING_PATTERNS_REPR;
    static String CUSTOM_PATTERNS_REPR;
    static int CUSTOM_PATTERN_COUNT;
    static String CUSTOM_PATTERN_NAMES;
    static String ROLLED_NUMBERS_REPR;
    static String USER_MARKED_NUM_REPR;
    static int PATTERN_COUNT;
    static int ROUNDS_COUNT;
    static String USERNAME;
    static String USER_CARDS_REPR;
    static String USER_CARD_PATTERNS_REPR;
    static String COMP_CARDS_REPR;
    static String COMP_CARD_PATTERNS_REPR;
    static int USER_CARD_COUNT;
    static int COMP_CARD_COUNT;
    static double USER_MONEY;
    static double COMP_MONEY;
    static int PLAYER_WIN_COUNT;
    static int COMP_WIN_COUNT;
    static int CARDS_BOUGHT;
    static double MAX_MONEY_HELD;

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
            cls();

            System.out.println("\t==== POST-GAME STATISTICS ====\n");
            System.out.println(
                    "> You held on for a total of " + ROUNDS_COUNT + ((ROUNDS_COUNT > 1) ? " rounds" : " round") + "!");
            System.out.println("> Maximum money held at some point: P" + MAX_MONEY_HELD);
            System.out.println("> Total number of cards bought: " + CARDS_BOUGHT);
            System.out.println("> Wins / Losses: " + PLAYER_WIN_COUNT + " / " + COMP_WIN_COUNT);
            System.out.println();

        } while (!isYesWhenPrompted("Exit the program?"));

        System.out.println(BINGOSHAKE + "\n");
        System.out.println("This was BINGO! Goodbye!");
    }

    static void letsPlayBingo() throws IOException, InterruptedException {
        cls();
        // WELCOME SCREEN
        System.out.println(BINGOSHAKE);
        System.out.println(BINGOASCII);
        System.out.println("WELCOME TO BINGO!");

        ROUNDS_COUNT = 1;
        USER_MONEY = COMP_MONEY = STARTING_MONEY;
        PLAYER_WIN_COUNT = 0;
        COMP_WIN_COUNT = 0;
        MAX_MONEY_HELD = 0;
        CARDS_BOUGHT = 0;
        CUSTOM_PATTERN_NAMES = "";
        CUSTOM_PATTERNS_REPR = "";
        CUSTOM_PATTERN_COUNT = 0;
        boolean isCreatingPattern;

        // NAME CREATION
        while (true) {
            System.out.print("\nWhat is your name?: ");
            USERNAME = SCANNER.nextLine();
            if (USERNAME.equalsIgnoreCase(COMP_NAME)) {
                System.out.println("You cannot name yourself as '" + USERNAME + "'.");
                System.out.println("Please use another name!\n");
                continue;
            } else if (USERNAME.isBlank() || USERNAME.isEmpty()) {
                System.out.println("Please include characters!");
                continue;
            }
            if (isYesWhenPrompted("You're sure with '" + USERNAME + "'?"))
                break;
        }

        cls();
        System.out.println(BINGOASCII);
        System.out.println("WELCOME, " + USERNAME + "!");

        // HELP MODULE
        if (isYesWhenPrompted("\nDo you want to go to the tutorial first?"))
            playTutorial();

        cls();

        USER_CARD_COUNT = COMP_CARD_COUNT = 1;

        do {
            // INITIALIZATION OF PER-ROUND VARIABLES
            USER_CARDS_REPR = "";
            USER_CARD_PATTERNS_REPR = "";
            COMP_CARDS_REPR = "";
            COMP_CARD_PATTERNS_REPR = "";
            ROLLED_NUMBERS_REPR = "";
            USER_MARKED_NUM_REPR = "";

            // MONETARY SYSTEM
            if (ROUNDS_COUNT > 1) {
                cls();
                System.out.println("Card (1x) -> \tP" + CARD_COST);
                System.out.println("Your money:\tP" + USER_MONEY);
                System.out.println(COMP_NAME + "'s money:\tP" + COMP_MONEY + "\n");
                while (true) {
                    System.out.print("How many cards do you want to buy?: ");
                    USER_CARD_COUNT = SCANNER.nextInt();
                    SCANNER.nextLine();
                    if (USER_CARD_COUNT * CARD_COST <= USER_MONEY)
                        break;
                    System.out.println("Insufficient money!\n");
                }
                USER_MONEY -= USER_CARD_COUNT * CARD_COST;
                CARDS_BOUGHT += USER_CARD_COUNT;
                System.out.println("\nYou bought " + USER_CARD_COUNT + ((USER_CARD_COUNT > 1) ? " cards" : " card")
                        + ". Remaining balance is P" + USER_MONEY);

                do {
                    COMP_CARD_COUNT = getRandomNumber(1, USER_CARD_COUNT + 2);
                } while (COMP_CARD_COUNT * CARD_COST > COMP_MONEY);
                COMP_MONEY -= COMP_CARD_COUNT * CARD_COST;
                System.out.println(COMP_NAME + " bought " + COMP_CARD_COUNT + ", with remaining P" + COMP_MONEY + ".");
            }

            createBingoCardsRepr(COMP_CARD_COUNT, COMP_NAME);
            createBingoCardsRepr(USER_CARD_COUNT, USERNAME);

            // CUSTOM PATTERNS
            if (CUSTOM_PATTERN_COUNT == 0) {
                isCreatingPattern = isYesWhenPrompted("\nDo you want to create custom winning patterns?");
            } else {
                if (!isYesWhenPrompted("Reuse custom patterns?")) CUSTOM_PATTERNS_REPR = CUSTOM_PATTERN_NAMES = "";
                isCreatingPattern = isYesWhenPrompted("Do you want to create more");
            }

            if (isCreatingPattern) patternCreation();
            CUSTOM_PATTERN_COUNT = occurenceOf(SEPARATOR_STRING, CUSTOM_PATTERNS_REPR);

            cls();

            // first game
            if (ROUNDS_COUNT == 0)
                System.out.println("You start with P" + STARTING_MONEY
                        + ", but you get to have one card for free in your first game!");

            // DEFAULT PATTERNS
            System.out.println("\nDEFAULT PATTERNS: X, VERTICAL, HORIZONTAL, DIAGONAL, AND BLACKOUT");
            if (CUSTOM_PATTERN_COUNT != 0)
                System.out.println("CUSTOM PATTERNS: " + CUSTOM_PATTERN_NAMES.substring(0, CUSTOM_PATTERN_NAMES.length() - 2));

            WINNING_PATTERNS_REPR = DEFAULT_WINNING_PATTERNS_REPR + CUSTOM_PATTERNS_REPR;
            PATTERN_COUNT = occurenceOf(SEPARATOR_STRING, WINNING_PATTERNS_REPR);

            // MAIN GAME LOOP
            printlnInteractive("\nTara BINGO!");
            bingoGameLoop();
            ROUNDS_COUNT++;

            if (USER_MONEY < CARD_COST) {
                printlnInteractive("\nGAME OVER! You don't have enough money to buy more cards!");
                System.out.println();
                break;
            } else if (COMP_MONEY < CARD_COST) {
                printlnInteractive("\nYOU WIN! Computer can't afford to buy any more cards!");
                System.out.println();
                break;
            }

        } while (isYesWhenPrompted("Do you want to play another round?"));
    }

    static void bingoGameLoop() throws IOException, InterruptedException {
        int randomNumber;
        char randomNumberRepr;

        int winningCardNo;
        int playerCheckingCounter;
        String checkedPlayer;

        char membership;
        double deduction = 2.5;
        boolean numberInCard;
        boolean numberIsInCardSaysUser;

        game:
        while (true) {
            cls();

            System.out.println(USERNAME + "'S " + ((USER_CARD_COUNT > 1) ? "CARDS" : "CARD") + ":");
            printCardsUpdatePatterns(USERNAME);
            System.out.println("\n" + COMP_NAME + ((COMP_CARD_COUNT > 1) ? "CARDS" : "CARD") + ":");
            printCardsUpdatePatterns(COMP_NAME);

            // Randomize in a 50-50 chance who to check first, which in turn is the one to
            // be called winner should they have the winning pattern.
            playerCheckingCounter = getRandomNumber(0, 2);

            for (int i = 0; i < 2; i++) {
                checkedPlayer = playerCheckingCounter == 1 ? USERNAME : COMP_NAME;
                winningCardNo = cardContainsWinningPattern(checkedPlayer);

                if (winningCardNo == -1) {
                    playerCheckingCounter = (playerCheckingCounter + 1) % 2;
                    continue;
                }

                System.out.println(BINGOASCII);
                System.out.println(checkedPlayer + " WINS!");
                printlnInteractive("WINNING CARD: No." + winningCardNo + "!");

                if (checkedPlayer.equals(COMP_NAME)) {
                    COMP_MONEY += PRIZE_PER_WIN;
                    COMP_WIN_COUNT++;
                } else {
                    USER_MONEY += PRIZE_PER_WIN;
                    PLAYER_WIN_COUNT++;
                    if (USER_MONEY > MAX_MONEY_HELD)
                        MAX_MONEY_HELD = USER_MONEY;
                }

                System.out.println(checkedPlayer + " wins P" + PRIZE_PER_WIN + ".\n");
                break game;
            }

            System.out.println("Taya taya...");

            do {
                randomNumberRepr = getNumberRepr(getRandomNumber(1, BINGO_MAX + 1));
            } while (ROLLED_NUMBERS_REPR.indexOf(randomNumberRepr) != -1);

            ROLLED_NUMBERS_REPR += randomNumberRepr;
            randomNumber = getReprNumber(randomNumberRepr);
            numberInCard = USER_CARDS_REPR.indexOf(randomNumberRepr) != -1;
            membership = BINGO.charAt((randomNumber - (randomNumber % 16)) / 15);

            for (int i = 0; i < getRandomNumber(50, 101); i++) {
                System.out.print("\rSa letra sang... ");
                System.out.print(BINGO.charAt(getRandomNumber(0, BINGO.length())));
                Thread.sleep(12);
            }

            System.out.print("\b" + membership + "!\n");

            for (int i = 0; i < getRandomNumber(25, 75); i++) {
                System.out.print(getRandomNumber(1, BINGO_MAX + 1) + "\r");
                Thread.sleep(25);
            }

            System.out.println(membership + " " + randomNumber + "!\n");
            numberIsInCardSaysUser = isYesWhenPrompted("Do you have " + randomNumber + " in any of your card?");

            System.out.println();

            if (numberInCard) {
                System.out.print("May ara ka sang " + randomNumber + ". ");
                if (numberIsInCardSaysUser) {
                    USER_MARKED_NUM_REPR += randomNumberRepr;
                    System.out.print("Markahan ang card...");
                } else {
                    System.out.println("Sayang, hindi pag markahan.");
                }
            } else {
                System.out.print("Wala ka sang " + randomNumber + ". ");
                if (numberIsInCardSaysUser) {
                    USER_MONEY -= deduction;
                    System.out.println("Buhinan imo kwarta P" + deduction + ".");
                    System.out.println("Remaining balance: P" + USER_MONEY);
                }
            }

            System.out.println();
            Thread.sleep(2500);
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

            if (player.equals(COMP_NAME)) {
                COMP_CARDS_REPR += card;
            } else {
                USER_CARDS_REPR += card;
            }
        }
    }

    static void printCardsUpdatePatterns(String player) {
        boolean isMarked;
        boolean isMiddle;
        boolean isPlayer = player.equals(USERNAME);
        int currentNum;
        int patternBits;
        int count;
        char currentNumRepr;
        String card_repr;

        if (isPlayer) {
            USER_CARD_PATTERNS_REPR = "";
            card_repr = USER_CARDS_REPR;
            count = USER_CARD_COUNT;
        } else {
            COMP_CARD_PATTERNS_REPR = "";
            card_repr = COMP_CARDS_REPR;
            count = COMP_CARD_COUNT;
        }

        // Use conditionals to further control the printing of the card.
        for (int i = 0; i < count; i++) {
            patternBits = 0;

            System.out.println("Card no. " + (i + 1));
            System.out.println("B" + GRID_SEP + "I" + GRID_SEP + "N" + GRID_SEP + "G" + GRID_SEP + "O");

            for (int j = 0; j < LENGTH; j++) {
                currentNumRepr = card_repr.charAt(j + (i * LENGTH));
                currentNum = getReprNumber(currentNumRepr);

                isMiddle = currentNumRepr == FREE_SPACE;
                isMarked = ROLLED_NUMBERS_REPR.indexOf(currentNumRepr) != -1 || isMiddle;

                if (isPlayer && USER_MARKED_NUM_REPR.indexOf(currentNumRepr) == -1 && !isMiddle)
                    isMarked = false;

                if (isMarked) {
                    System.out.print(isMiddle ? "FS" : ("(" + currentNum + ")"));
                    patternBits |= 1 << (LENGTH - j - 1);
                } else {
                    System.out.print(currentNum);
                }

                if (j % 5 == 4) {
                    System.out.print('\n');
                } else {
                    System.out.print(GRID_SEP);
                }
            }

            if (player.equals(COMP_NAME)) {
                COMP_CARD_PATTERNS_REPR += patternBits + SEPARATOR_STRING;
            } else {
                USER_CARD_PATTERNS_REPR += patternBits + SEPARATOR_STRING;
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
         * shifted by i, e.g. 01 | 1 << 1 (index 1) --> 01 | 10 --> 11
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
        int currentSelection = MSB_INDEX;
        boolean isInTool = true;

        String action;

        do {
            cls();
            // ASCII art font: (Outline) Big by Glenn Chappell
            System.out.println("""
                     ____ _____ _   _  _____  ____                \s
                    |  _ \\_   _| \\ | |/ ____|/ __ \\               \s
                    | |_) || | |  \\| | |  __| |  | |              \s
                    |  _ < | | | . ` | | |_ | |  | |              \s
                    | |_) || |_| |\\  | |__| | |__| |              \s
                    |____/_____|_|_\\_|\\_____|\\____/__ _____  _   _\s
                    |  __ \\ /\\|__   __|__   __|  ____|  __ \\| \\ | |
                    | |__) /  \\  | |     | |  | |__  | |__) |  \\| |
                    |  ___/ /\\ \\ | |     | |  |  __| |  _  /| . ` |
                    | |  / ____ \\| |     | |  | |____| | \\ \\| |\\  |
                    |_| /_/    \\_\\_| _  _|_|__|______|_|  \\_\\_| \\_|
                    |  \\/  |   /\\   | |/ /  ____|  __ \\           \s
                    | \\  / |  /  \\  | ' /| |__  | |__) |          \s
                    | |\\/| | / /\\ \\ |  < |  __| |  _  /           \s
                    | |  | |/ ____ \\| . \\| |____| | \\ \\           \s
                    |_|  |_/_/    \\_\\_|\\_\\______|_|  \\_\\          \s
                    """);

            System.out.println("B" + GRID_SEP + "I" + GRID_SEP + "N" + GRID_SEP + "G" + GRID_SEP + "O\n");

            // PRINTING THE CURRENT PATTERN MAKER CARD
            for (int i = MSB_INDEX; i >= 0; i--) {
                // if bit AND'd with ith shift is not 0, the current character is marked
                currChar = ((bits & 1 << i) != 0 || i == MIDDLE) ? '*' : '-';

                if (i == currentSelection) {
                    System.out.print("[" + currChar + "]");
                } else {
                    System.out.print("" + currChar);
                }

                if (i % 5 == 0) {
                    System.out.println("\n");
                } else {
                    System.out.print(GRID_SEP);
                }
            }

            System.out.println("[wasd]\tMove selection");
            System.out.println("[" + markUnmarkSqr + "]\tFlip selection");
            System.out.println("[" + markUnmarkRow + "]\tFlip whole row");
            System.out.println("[" + markUnmarkCol + "]\tFlip whole column");
            System.out.println("[" + useCustomPatt + "]\tAdd as winning pattern");
            System.out.println("[" + resetPattern + "]\tReset pattern");
            System.out.println("[" + exitPattTool + "]\tExit Tool");

            while (true) {
                System.out.print("\nAction: ");
                action = SCANNER.nextLine().toLowerCase().strip();

                if (action.equals(markUnmarkSqr)) {
                    bits ^= 1 << currentSelection;

                } else if (action.equals("w") && currentSelection < 20) {
                    currentSelection += 5;
                } else if (action.equals("a") && currentSelection % 5 < 4) {
                    currentSelection += 1;
                } else if (action.equals("s") && currentSelection > 4) {
                    currentSelection -= 5;
                } else if (action.equals("d") && currentSelection % 5 > 0) {
                    currentSelection -= 1;

                } else if (action.equals(useCustomPatt)) {
                    if (bits == 0) {
                        printInteractive("Please mark something in the pattern.");
                        break;
                    }
                    System.out.print("What would you like to name the pattern?: ");
                    String customPatternName = SCANNER.nextLine();
                    CUSTOM_PATTERNS_REPR += bits + SEPARATOR_STRING;
                    CUSTOM_PATTERN_NAMES += "\"" + customPatternName + "\", ";
                    printInteractive("Added '" + customPatternName + "' as a winning pattern.");
                    if (isYesWhenPrompted("Reset the grid?")) {
                        bits = 0;
                        currentSelection = MSB_INDEX;
                    }

                } else if (action.equals(resetPattern)) {
                    if (isYesWhenPrompted("Are you sure you want to reset?")) {
                        bits = 0;
                        currentSelection = MSB_INDEX;
                    }

                } else if (action.equals(exitPattTool)) {
                    if (bits > 0) {
                        isInTool = !isYesWhenPrompted("Are you sure you want to discard changes and exit?");
                    } else {
                        isInTool = !isYesWhenPrompted("Are you sure you want to exit?");
                    }
                    printInteractive("Exiting Pattern Maker Tool...");

                } else if (action.equals(markUnmarkRow)) {
                    // leftIndex is the index of the current row's leftmost square
                    // (actually calculates the rightmost since we iterate backwards)
                    leftIndex = currentSelection - (currentSelection % 5);
                    for (int j = 0; j < 5; j++) {
                        bits ^= 1 << leftIndex + j;
                    }

                } else if (action.equals(markUnmarkCol)) {
                    // topIndex is the index of the current column's topmost square
                    // (is not reversed since up is up regardless of direction)
                    topIndex = currentSelection % 5;
                    for (int k = 0; k < 5; k++) {
                        bits ^= 1 << topIndex + k * 5;
                    }

                } else {
                    System.out.println("Invalid input.");
                    continue;
                }
                break;
            }
        } while (isInTool);
    }

    static int cardContainsWinningPattern(String player) {
        String cardPatternsRepr;
        int cardCount;

        if (player.equals(COMP_NAME)) {
            cardPatternsRepr = COMP_CARD_PATTERNS_REPR;
            cardCount = COMP_CARD_COUNT;
        } else {
            cardPatternsRepr = USER_CARD_PATTERNS_REPR;
            cardCount = USER_CARD_COUNT;
        }

        int winningPatternBits, cardPatternBits;
        int nextWinningPatternIndex = 0, nextCardPatternIndex;
        String winningPatternRepr, patternRepr;
        char currentWinningPatternChar, currentCardPatternChar;

        for (int patternCount = 0; patternCount < PATTERN_COUNT; patternCount++) {
            winningPatternRepr = "";
            nextCardPatternIndex = 0;

            for (int i = 0; i < WINNING_PATTERNS_REPR.length(); i++) {
                currentWinningPatternChar = WINNING_PATTERNS_REPR.charAt(i + nextWinningPatternIndex);
                if (currentWinningPatternChar == SEPARATOR_CHAR)
                    break;
                winningPatternRepr += currentWinningPatternChar;
            }
            nextWinningPatternIndex += winningPatternRepr.length() + 1;
            winningPatternBits = Integer.parseInt(winningPatternRepr);

            for (int count = 0; count < cardCount; count++) {
                patternRepr = "";

                for (int j = 0; j < cardPatternsRepr.length(); j++) {
                    currentCardPatternChar = cardPatternsRepr.charAt(j + nextCardPatternIndex);
                    if (currentCardPatternChar == SEPARATOR_CHAR)
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
                bits |= (1 << LENGTH - 1 - i);
        }
        return bits;
    }

    static void playTutorial() throws IOException, InterruptedException {
        cls();
        printlnInteractive("Hello! Welcome to the BINGO tutorial!");

        // Limit the output to about 50 characters.
        tutorial:
        while (true) {
            cls();
            System.out.println("BINGO TUTORIAL\n");
            System.out.println("""
                    What do you want to know?
                    1. Base Mechanics
                    2. Money and Buying Cards
                    3. Creating Patterns
                    4. Enter Game
                    """);
            System.out.print("Enter your choice: ");

            switch (SCANNER.nextLine()) {

                case ("1") -> {
                    cls();
                    System.out.println("BASE MECHANICS\n");
                    printlnInteractive("BINGO is a game of chance.");
                    printlnInteractive("Once every turn, a number is rolled randomly.");
                    printlnInteractive("""
                            The middle, denoted FS, means free square.
                            Consider it already marked.""");
                    System.out.println();
                    cls();

                    printlnInteractive("""
                            After a roll,
                            on a BINGO card that looks like this:

                            B\tI\tN\tG\tO
                            12\t22\t43\t51\t72
                            5\t23\t35\t57\t61
                            9\t28\tFS\t48\t69
                            11\t29\t41\t49\t62
                            6\t19\t42\t50\t65

                            You are prompted if the number is in one of your cards.""");
                    printlnInteractive("\n> IF you say yes and it is, the square gets marked.");
                    printlnInteractive("> If you say yes but it isn't, your money gets deducted.");
                    printlnInteractive("> If you say no but it is, it will not get marked.");
                    printlnInteractive("> IF you say no and it isn't, nothing will happen.");

                    cls();
                    printlnInteractive("""
                            B\tI\tN\tG\tO
                            12\t22\t43\t51\t72
                            5\t23\t35\t57\t61
                            9\t28\tFS\t48\t69
                            11\t29\t41\t49\t62
                            6\t19\t42\t50\t65
                            """);

                    for (int i = 0; i < getRandomNumber(50, 76); i++) {
                        System.out.print("\rSa letra sang... " + BINGO.charAt(getRandomNumber(0, 5)));
                        Thread.sleep(25);
                    }

                    printInteractive("\bO!");

                    printlnInteractive("69!");

                    if (isYesWhenPrompted("Is 69 in the card?")) {
                        printlnInteractive("""

                                You bet it is!
                                May 69 ka!

                                B\tI\tN\tG\tO
                                12\t22\t43\t51\t72
                                5\t23\t35\t57\t61
                                9\t28\tFS\t48\t(69)
                                11\t29\t41\t49\t62
                                6\t19\t42\t50\t65

                                [NOTE: The number was marked]""");
                    } else {
                        printlnInteractive("""

                                Di mo nakita. may ara ka 69!

                                B\tI\tN\tG\tO
                                12\t22\t43\t51\t72
                                5\t23\t35\t57\t61
                                9\t28\tFS\t48\t69
                                11\t29\t41\t49\t62
                                6\t19\t42\t50\t65

                                [NOTE: The number was not marked.]""");
                    }

                    cls();
                    printlnInteractive("The game has two types of patterns:");
                    printInteractive("""

                            1. CUSTOMIZED PATTERNS
                            - The host is prompted whether to create custom patterns.
                            - If the host does not wish so, the game uses back to default patterns.
                            - The game is won if one of the host's customized patterns is achieved.
                            """);
                    printlnInteractive("""

                            2. DEFAULT PATTERNS
                            - The game is won if five (5) marks in a row is achieved,
                            either vertically

                            B\tI\tN\tG\tO
                            (12)\t22\t43\t51\t72
                            (5)\t23\t35\t57\t61
                            (9)\t28\tFS\t48\t69
                            (11)\t29\t41\t49\t62
                            (6)\t19\t42\t50\t65

                            (in any column),

                            horizontally

                            B\tI\tN\tG\tO
                            12\t22\t43\t51\t72
                            5\t23\t35\t57\t61
                            (9)\t(28)\tFS\t(48)\t(69)
                            11\t29\t41\t49\t62
                            6\t19\t42\t50\t65

                            (in any row),

                            or diagonally

                            B\tI\tN\tG\tO
                            12\t22\t43\t51\t(72)
                            5\t23\t35\t(57)\t61
                            9\t28\tFS\t48\t69
                            11\t(29)\t41\t49\t62
                            (6)\t19\t42\t50\t65

                            or special states like the CROSS (intersecting diagonals) or BLACKOUT (full card).""");

                    cls();
                    System.out.println("That's all for the base game mechanics!");
                    printlnInteractive("Explore more of the tutorial or go straight to the game!");
                }

                case "2" -> {
                    cls();
                    System.out.println("MONEY AND BUYING CARDS\n");
                    System.out.println("You enter with P" + STARTING_MONEY);
                    printlnInteractive("But you start the game with one free card.");
                    printlnInteractive("Once the first game ends, you can buy cards.");

                    cls();
                    printlnInteractive("\n1 card = P" + CARD_COST);
                    printlnInteractive("\nAfter a game, the winner gets P" + PRIZE_PER_WIN);
                    printInteractive("""
                            You can buy new cards as much as you want
                            as long as you can afford it.""");
                    printInteractive("""
                            If you or the computer reaches zero money,
                            then the game is over.""");
                    printlnInteractive("That's all about money system and buying cards!");
                }

                case "3" -> {
                    cls();
                    System.out.println("CREATING PATTERNS\n");
                    printlnInteractive("Welcome, Host!");
                    printlnInteractive("You can create patterns with the Pattern Maker Tool.");
                    printlnInteractive("""
                            PATTERN MAKER TOOL

                            B\tI\tN\tG\tO
                            [-]\t-\t-\t-\t-
                            -\t-\t-\t-\t-
                            -\t-\tFS\t-\t-
                            -\t-\t-\t-\t-
                            -\t-\t-\t-\t-

                            Action: (user input)
                            """);

                    cls();
                    System.out.println("[NOTE: All keys are inputted by typing into the terminal and then pressing ENTER]");
                    printlnInteractive("\nFirst, select which square you want to mark as part of the winning pattern.\n");
                    printlnInteractive("To move the selection:");
                    printlnInteractive("'w' - moves upward.");
                    printlnInteractive("'s' - moves downward.");
                    printlnInteractive("'a' - moves to the left.");
                    printlnInteractive("'d' - moves to the right.");

                    printlnInteractive("\nThere are several ways we can modify the pattern:");
                    printlnInteractive("'" + markUnmarkSqr + "' - mark the current square;");
                    printlnInteractive("'" + markUnmarkCol + "' - mark the whole column where the selection lies;");
                    printlnInteractive("'" + markUnmarkRow + "' - mark the whole row where the selections lies;");
                    printlnInteractive("'" + resetPattern + "' - reset all the customized pattern;");
                    printlnInteractive("'" + exitPattTool + "' - if you're done or you don't want to make patterns.");
                }
                case "4" -> {
                    cls();
                    printlnInteractive("You're all settled! Good luck and have fun!");
                    break tutorial;
                }
                default -> printlnInteractive("\nInvalid input!");
            }
        }
        cls();
    }

    static int occurenceOf(String target, String in) {
        return in.length() - in.replace(target, "").length();
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
        return (char) (number + ASCII_MIN);
    }

    static int getReprNumber(char repr) {
        return repr - ASCII_MIN;
    }

    static void printlnInteractive(String s) {
        System.out.println(s + " (ENTER)");
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
