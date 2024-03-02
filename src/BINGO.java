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

    // " " (32 in ASCII) AS SEPARATOR FOR PATTERN AND "!" AS FREE SPACE IN REPR
    static final char SEPCHAR = 32;
    static final char FREE_SPACE = SEPCHAR + 1;
    static final String SEPSTR = SEPCHAR + "";
    static final String GRID_SEP = "\t";

    static final int ASCIIMIN = FREE_SPACE + 1;
    static final int BINGOMAX = 75;
    static final int LENGTH = 25;
    static final int MIDDLE = 12;
    static final int MSBIndex = LENGTH - 1;

    static final double CARD_COST = 5;
    static final double PRIZE_PER_WIN = 20;
    static final double STARTING_MONEY = 25;

    // keybindings for the pattern maker tool
    static final String markUnmarkSq = "q";
    static final String markWholeCol = "z";
    static final String markWholeRow = "x";
    static final String resetPattern = "r";
    static final String exitPattTool = "e";

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
    static int ROUNDS_COUNT;

    static String USERNAME;
    static String USER_CARDS_REPR;
    static String USER_CARD_PATTERNS_REPR;

    static final String COMP_NAME = "COMP";
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
        // WELCOME SCREEN
        System.out.println(BINGOSHAKE);
        System.out.println(BINGOASCII);

        ROUNDS_COUNT = 1;
        USER_MONEY = COMP_MONEY = STARTING_MONEY;
        PLAYER_WIN_COUNT = 0;
        COMP_WIN_COUNT = 0;
        MAX_MONEY_HELD = 0;
        CARDS_BOUGHT = 0;

        // NAME CREATION
        while (true) {
            System.out.print("\nWhat would you like to name yourself?: ");
            USERNAME = SCANNER.nextLine();
            if (USERNAME.equalsIgnoreCase(COMP_NAME)) {
                System.out.println("You cannot name yourself as '" + USERNAME + "'!");
                System.out.println("Please use another name!\n");
                continue;
            } else if (USERNAME.isBlank() || USERNAME.isEmpty()) {
                System.out.println("Please include characters!");
                continue;
            }
            if (isYesWhenPrompted("You're sure with '" + USERNAME + "'?"))
                break;
        }

        System.out.println("\nWELCOME, " + USERNAME + "!");

        // HELP MODULE
        if (isYesWhenPrompted("\nDo you want to go to the turorial first?"))
            playTutorial();
        System.out.println(
                "\nYou start with P" + STARTING_MONEY + ", but you get to have one card for free in your first game!");
        USER_CARD_COUNT = COMP_CARD_COUNT = 1;

        do {
            if (USER_MONEY < CARD_COST) {
                printlnInteractive("\nGAME OVER! You don't have enough money to buy more cards!");
                System.out.println();
                break;
            } else if (COMP_MONEY < CARD_COST) {
                printlnInteractive("\nYOU WIN! Computer can't afford to buy any more cards!");
                System.out.println();
                break;
            }

            // INITIALIZATION OF VARIABLES
            USER_CARDS_REPR = "";
            USER_CARD_PATTERNS_REPR = "";
            COMP_CARDS_REPR = "";
            COMP_CARD_PATTERNS_REPR = "";
            WINNING_PATTERNS_REPR = "";
            ROLLED_NUMBERS_REPR = "";
            PATTERN_COUNT = 0;

            // TOOL TUTORIAL
            // patternCreationTutorial();

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
                    COMP_CARD_COUNT = getRandomNumber(1, USER_CARD_COUNT + 3);
                } while (COMP_CARD_COUNT * CARD_COST > COMP_MONEY);
                COMP_MONEY -= COMP_CARD_COUNT * CARD_COST;
                System.out.println(COMP_NAME + " bought " + COMP_CARD_COUNT + ", with remaining P" + COMP_MONEY + ".");
            }

            createBingoCardsRepr(COMP_CARD_COUNT, COMP_NAME);
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
                System.out.println("\nDEFAULT WINNING PATTERNS: X, VERTICAL, HORIZONTAL, AND BLACKOUT");
                WINNING_PATTERNS_REPR += convertPattToInt("*---*-*-*---*---*-*-*---*") + SEPSTR;
                WINNING_PATTERNS_REPR += convertPattToInt("*************************") + SEPSTR;
                WINNING_PATTERNS_REPR += convertPattToInt("*----*----*----*----*----") + SEPSTR; // Vertical 1
                WINNING_PATTERNS_REPR += convertPattToInt("-*----*----*----*----*---") + SEPSTR; // vrow 2
                WINNING_PATTERNS_REPR += convertPattToInt("--*----*----*----*----*--") + SEPSTR; // vRow 3
                WINNING_PATTERNS_REPR += convertPattToInt("---*----*----*----*----*-") + SEPSTR; // vRow 4
                WINNING_PATTERNS_REPR += convertPattToInt("----*----*----*----*----*") + SEPSTR; // vRow 5
                WINNING_PATTERNS_REPR += convertPattToInt("*****--------------------") + SEPSTR; // Horizontal 1
                WINNING_PATTERNS_REPR += convertPattToInt("-----*****---------------") + SEPSTR; // hRow 2
                WINNING_PATTERNS_REPR += convertPattToInt("----------*****----------") + SEPSTR; // hRow 3
                WINNING_PATTERNS_REPR += convertPattToInt("---------------*****-----") + SEPSTR; // hRow 4
                WINNING_PATTERNS_REPR += convertPattToInt("--------------------*****") + SEPSTR; // hRow 5
                PATTERN_COUNT = 12;
            }

            // MAIN GAME LOOP
            printlnInteractive("\nTara BINGO!");
            bingoGameLoop();
            ROUNDS_COUNT++;

        } while (isYesWhenPrompted("Do you want to play another round?"));
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

                if (winningCardNo != -1) {
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

            if (player.equals(COMP_NAME)) {
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

        if (player.equals(COMP_NAME)) {
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
                    patternBits |= 1 << (LENGTH - j - 1);

                if (j % 5 == 4) {
                    System.out.print('\n');
                } else {
                    System.out.print(GRID_SEP);
                }
            }

            if (player.equals(COMP_NAME)) {
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
            System.out.println("[" + markUnmarkSq + "] Mark/unmark current selection");
            System.out.println("[" + markWholeRow + "] Mark whole row");
            System.out.println("[" + markWholeCol + "] Mark whole column");
            // System.out.println("[n] Discard and exit");
            System.out.println("[" + resetPattern + "] Reset");
            System.out.println("[" + exitPattTool + "] Done/Exit");

            while (true) {
                System.out.print("\nAction: ");
                action = SCANNER.nextLine().toLowerCase().strip();

                if (action.equals(markUnmarkSq)) {
                    // mark/unmark current selection
                    bits ^= 1 << currentSelection;
                } else if (action.equals("w") && currentSelection < 20) {
                    currentSelection += 5;
                } else if (action.equals("a") && currentSelection % 5 < 4) {
                    currentSelection += 1;
                } else if (action.equals("s") && currentSelection > 4) {
                    currentSelection -= 5;
                } else if (action.equals("d") && currentSelection % 5 > 0) {
                    currentSelection -= 1;
                } else if (action.equals(resetPattern)) {
                    bits = 0;
                    currentSelection = MSBIndex;
                } else if (action.equals(exitPattTool)) {
                    System.out.println();
                    isInTool = false;
                } else if (action.equals(markWholeRow)) {
                    // leftIndex is the index of the current row's leftmost square
                    // (actually calculates the rightmost since we iterate backwards)
                    leftIndex = currentSelection - (currentSelection % 5);
                    for (int j = 0; j < 5; j++) {
                        bits |= 1 << leftIndex + j;
                    }
                } else if (action.equals(markWholeCol)) {
                    // topIndex is the index of the current column's topmost square
                    // (is not reversed since up is up regardless of direction)
                    topIndex = currentSelection % 5;
                    for (int k = 0; k < 5; k++) {
                        bits |= 1 << topIndex + k * 5;
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

        if (player.equals(COMP_NAME)) {
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
                bits |= (1 << LENGTH - 1 - i);
        }
        return bits;
    }


    static void mainOption(){
        Scanner scan = new Scanner(System.in);

        String subOption = ("\n1. Enter New Game"+ "\n2. Back to Menu");
                System.out.println(subOption);
                System.out.print("Enter your choice: ");
                int option = scan.nextInt();
                
                if (option == 1){
                    printlnInteractive("You're all settled. Good luck and have fun!");
                }else if (option == 2){
                    playTutorial();
                }else{
                    printlnInteractive("Invalid input.");
                    mainOption();
                }
    }

    static void playTutorial(){
        System.out.println("Hello! Welcome to the BINGO tutorial!");
        tutorials();

    }

    static void tutorials() {
        // Queenielyn
        Scanner scan = new Scanner(System.in);
        String tutorial = ( "What do you want to know?" + "\n1. Mechanics"+"\n2. Buy Cards" + 
        "\n3. Create Patterns" + "\n4. Enter Game");
        System.out.println(tutorial);
        System.out.print("Enter your choice: ");
        int choice = scan.nextInt();

        if (choice == 1){
            printInteractive("Mechanics");
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
            The game has 2 winning patterns:
                1. The Default Patterns:
                    The game is won if five (5) marks in a row is achieved,
                    either vertically, horizontally, or diagonally.
                2. Customized Patterns
                    The game is won if one if the host's customized patterns is achieved.
            """);
            printlnInteractive("The winner gets P20.00 after the game ends.");
            printlnInteractive("If the user or the computer reaches P0.00, then the game will be over.");
            
            mainOption();

        }else if (choice == 2){
            printlnInteractive("Buy Cards");
            printlnInteractive("Once the first game ends, you can buy new cards");
            printlnInteractive("1 card = P5.00");
            printlnInteractive("You can buy new cards as much as you want as long as it's within your current money.");
            mainOption();
        }else if (choice == 3){
            printlnInteractive("Create Patterns");
            printlnInteractive("Welcome, Host!");
            printlnInteractive("You can create patterns with the Pattern Maker TOol.");
            printlnInteractive("""
                Pattern Maker Tool
                (-)\t-\t-\t-\t-\n
                -\t-\t-\t-\t-\n
                -\t-\tB\t-\t-\n
                -\t-\t-\t-\t-\n
                -\t-\t-\t-\t-\n 
            """);
            printlnInteractive("First, select which coordinates you want to mark as part of the winning pattern.");
            printlnInteractive("To move the selection type: ");
            printlnInteractive("'w' - moves upward");
            printlnInteractive("'s' - moves downward");
            printInteractive("'a' - moves to the left");
            printlnInteractive("'d' - moves to the right.");
            printlnInteractive("You can mark the whole row by typing 'x'.");
            printlnInteractive("You can mark the whole column by typing 'z'.");
            printlnInteractive("You can reset all the customized pattern by typing 'r'.");
            printlnInteractive("If you're done or you don't want to make patterns, type 'e'.");
            mainOption();

        }else if (choice == 4){
            printlnInteractive("You're all settled. Good luck and have fun!");
        }else{
            printlnInteractive("Invalid input!");
            playTutorial();

        }
        
        
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
