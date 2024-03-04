import java.io.IOException;
import java.util.Scanner;
import java.util.Random;

public class BINGO {
    // BINGO by us.
    // Best played in a vertical terminal.

    /*
     * We limited ourselves with discussed programming constructs and the allowed
     * Random module for as much as possible. But alas we have utilized some
     * higher-level techniques, for aesthetic, code reusability, and code
     * maintainability purposes (functions, clearing of the terminal, Thread sleep
     * printing, etc.).
     *
     * While you may want to gouge your eyes after looking at the code, do know the
     * deficiency and limitations in the knowledge of the programmers and the
     * desired goal of compliance with the requirements of the project.
     *
     * Good day and have fun playing BINGO!
     */

    // GLOBAL VARIABLES
    static final Random RANDOM = new Random();
    static final Scanner SCANNER = new Scanner(System.in);
    static final ProcessBuilder CMD_PROCESS = new ProcessBuilder("cmd", "/c", "cls").inheritIO();

    // " " (32 in ASCII) AS SEPARATOR FOR PATTERN AND "!" AS FREE SPACE IN REPR
    static final char SEPARATOR_CHAR = ' ';
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
    static final double DEDUCTION = 2.5;
    static final double STARTING_MONEY = 25;
    static final String COMP_NAME = "COMP";

    // keybindings for the pattern maker tool
    static final String markUnmarkSqr = "q";
    static final String markUnmarkRow = "z";
    static final String markUnmarkCol = "x";
    static final String useCustomPatt = "t";
    static final String resetPattern = "r";
    static final String exitPattTool = "e";

    // @formatter:off
    static final String DEFAULT_WINNING_PATTERNS_REPR = (
              convertPattToInt("*---*-*-*---*---*-*-*---*") + SEPARATOR_STRING // cross
            + convertPattToInt("*************************") + SEPARATOR_STRING // blackout
            + convertPattToInt("*----*----*----*----*----") + SEPARATOR_STRING // col 1
            + convertPattToInt("-*----*----*----*----*---") + SEPARATOR_STRING // col 2
            + convertPattToInt("--*----*----*----*----*--") + SEPARATOR_STRING // col 3
            + convertPattToInt("---*----*----*----*----*-") + SEPARATOR_STRING // col 4
            + convertPattToInt("----*----*----*----*----*") + SEPARATOR_STRING // col 5
            + convertPattToInt("*****--------------------") + SEPARATOR_STRING // row 1
            + convertPattToInt("-----*****---------------") + SEPARATOR_STRING // row 2
            + convertPattToInt("----------*****----------") + SEPARATOR_STRING // row 3
            + convertPattToInt("---------------*****-----") + SEPARATOR_STRING // row 4
            + convertPattToInt("--------------------*****") + SEPARATOR_STRING // row 5
    );
    // @formatter:on

    /*
     * There is NO SEPARATOR between card reprs, only in pattern reprs, since we can
     * control the printing of card repr, and there is no need to print patterns
     * (for now).
     *
     * if using substrings: Substring of each reprs and sequences is [start, start +
     * LENGTH], where start = i * LENGTH. However, if you decided to add separation,
     * use start = i * LENGTH + (i * 1).
     */

    static String USERNAME;
    static String ROLLED_NUMBERS_REPR;
    static String USER_MARKED_NUM_REPR;
    static String WINNING_PATTERNS_REPR;
    static String CUSTOM_PATTERNS_REPR;
    static String CUSTOM_PATTERN_NAMES;
    static String USER_CARDS_REPR;
    static String COMP_CARDS_REPR;
    static String USER_CARD_PATTERNS_REPR;
    static String COMP_CARD_PATTERNS_REPR;
    static int WINNING_PATTERN_COUNT;
    static int CUSTOM_PATTERN_COUNT;
    static int USER_CARD_COUNT;
    static int COMP_CARD_COUNT;
    static int ROUND_COUNT;
    static int PLAYER_WIN_COUNT;
    static int COMP_WIN_COUNT;
    static int CARDS_BOUGHT;
    static double USER_MONEY;
    static double COMP_MONEY;
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
            System.out.println("> You held on for " + ROUND_COUNT + "round" + ((ROUND_COUNT > 1) ? "s!" : "!"));
            System.out.println("> Maximum money held at some point: P" + MAX_MONEY_HELD);
            System.out.println("> Total number of cards bought: " + CARDS_BOUGHT);
            System.out.println("> Wins / Losses: " + PLAYER_WIN_COUNT + " / " + COMP_WIN_COUNT);

        } while (!isYesWhenPrompted("\nExit the program?"));

        System.out.println(BINGOSHAKE + "\n");
        System.out.println("This was BINGO! Goodbye!");
    }

    static void letsPlayBingo() throws IOException, InterruptedException {
        cls();
        // WELCOME SCREEN

        System.out.println(BINGOSHAKE);
        System.out.println(BINGOASCII);
        System.out.println("WELCOME TO BINGO!");

        ROUND_COUNT = 1;
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
            if (ROUND_COUNT > 1) {
                cls();
                System.out.println(BINGOASCII);
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

            cls();
            System.out.println(BINGOASCII);

            // CUSTOM PATTERNS
            if (CUSTOM_PATTERN_COUNT == 0) {
                isCreatingPattern = isYesWhenPrompted("Do you want to create custom winning patterns?");
            } else {
                if (!isYesWhenPrompted("Reuse custom patterns?")) {
                    CUSTOM_PATTERNS_REPR = CUSTOM_PATTERN_NAMES = "";
                }
                isCreatingPattern = isYesWhenPrompted("Do you want to create new custom patterns?");
            }

            if (isCreatingPattern)
                patternCreation();
            CUSTOM_PATTERN_COUNT = occurenceOf(SEPARATOR_STRING, CUSTOM_PATTERNS_REPR);

            cls();
            System.out.println(BINGOASCII);

            // first game
            if (ROUND_COUNT == 0) {
                System.out.println("You start with P" + STARTING_MONEY
                        + ", but you get to have one card for free in your first game!\n");
            }

            // DEFAULT PATTERNS
            randomizeDefaultWinningPattern();
            WINNING_PATTERNS_REPR += CUSTOM_PATTERNS_REPR;
            WINNING_PATTERN_COUNT = occurenceOf(SEPARATOR_STRING, WINNING_PATTERNS_REPR);

            if (CUSTOM_PATTERN_COUNT != 0)
                System.out.println("CUSTOM PATTERN" + ((CUSTOM_PATTERN_COUNT > 1) ? "S: " : ": ")
                        + CUSTOM_PATTERN_NAMES.substring(0, CUSTOM_PATTERN_NAMES.length() - 2));

            // MAIN GAME LOOP
            printInteractive("\nTara BINGO!");
            bingoGameLoop();
            ROUND_COUNT++;

            if (USER_MONEY < CARD_COST) {
                printInteractive("\nGAME OVER! You don't have enough money to buy more cards!");
                System.out.println();
                break;
            } else if (COMP_MONEY < CARD_COST) {
                printInteractive("\nYOU WIN! Computer can't afford to buy any more cards!");
                System.out.println();
                break;
            }

        } while (isYesWhenPrompted("Do you want to play another round?"));
    }

    static void bingoGameLoop() throws IOException, InterruptedException {
        int randomNumber;
        int winningCardNo;
        int playerCheckingCounter;
        char letterMembership;
        char randomNumberRepr;
        String checkedPlayer;
        boolean numberIsInCardSaysUser;
        int chance = 3;

        game: while (true) {
            cls();

            System.out.println("**** " + USERNAME + "'S " + ((USER_CARD_COUNT > 1) ? "CARDS" : "CARD") + " ****");
            printCardsUpdatePatterns(USERNAME);
            System.out.println("\n**** " + COMP_NAME + "'S " + ((COMP_CARD_COUNT > 1) ? "CARDS" : "CARD") + " ****");
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
                printInteractive("WINNING CARD: No." + winningCardNo + "!");

                if (checkedPlayer.equals(COMP_NAME)) {
                    COMP_MONEY += PRIZE_PER_WIN;
                    COMP_WIN_COUNT++;
                } else {
                    USER_MONEY += PRIZE_PER_WIN;
                    PLAYER_WIN_COUNT++;
                    if (USER_MONEY > MAX_MONEY_HELD)
                        MAX_MONEY_HELD = USER_MONEY;

                    // not supported in terminal
                    // System.out.println("""
                    // ⠀⠀⠀⠀⠀⢀⠤⠐⠒⠀⠀⠀⠒⠒⠤⣀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
                    // ⠀⠀⠀⡠⠊⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠑⢄⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
                    // ⠀⠀⡔⠁⠀⠀⠀⠀⠀⢰⠁⠀⠀⠀⠀⠀⠀⠈⠆⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
                    // ⠀⢰⠀⠀⠀⠀⠀⠀⠀⣾⠀⠀⠔⠒⠢⠀⠀⠀⢼⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
                    // ⠀⡆⠀⠀⠀⠀⠀⠀⠀⠸⣆⠀⠀⠙⠀⠀⠠⠐⠚⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
                    // ⠀⠇⠀⠀⠀⠀⠀⠀⠀⠀⢻⠀⠀⠀⠀⠀⠀⡄⢠⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡀⠀⠀
                    // ⠀⢸⠀⠀⠀⠀⠀⠀⠀⠀⢸⠀⠀⠀⠀⣀⣀⡠⡌⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⡀⢄⣲⣬⣶⣿⣿⡇⡇⠀
                    // ⠀⠀⠆⠀⠀⠀⠀⠀⠀⠀⠘⡆⠀⠀⢀⣀⡀⢠⠃⠀⠀⠀⠀⠀⠀⠀⠀⠀⢴⣾⣶⣿⣿⣿⣿⣿⣿⣿⣿⡇⠀
                    // ⠀⠀⢸⠀⠀⠀⠀⠠⢄⠀⠀⢣⠀⠀⠑⠒⠂⡌⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠛⢿⣿⣿⣿⣿⣿⣿⣿⡇⠀
                    // ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠑⠤⡀⠑⠀⠀⠀⡘⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⡀⣡⣿⣿⣿⣿⣿⣿⣿⣇⠀
                    // ⠀⠀⢀⡄⠀⠀⠀⠀⠀⠀⠀⠈⢑⠖⠒⠉⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠐⣴⣿⣿⣿⡟⠁⠈⠛⠿⣿⠀
                    // ⠀⣰⣿⣿⣄⠀⠀⠀⠀⠀⠀⠀⢸⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⢈⣾⣿⣿⣿⠏⠀⠀⠀⠀⠀⠈⠀
                    // ⠈⣿⣿⣿⣿⣷⡤⣀⡀⠀⠀⢀⠎⣦⣄⣀⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣢⣿⣿⣿⡿⠃⠀⠀⠀⠀⠀⠀⠀⠀
                    // ⠀⠘⣿⣿⣿⣿⣿⣄⠈⢒⣤⡎⠀⢸⣿⣿⣿⣷⣶⣤⣄⣀⠀⠀⠀⢠⣽⣿⠿⠿⣿⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀
                    // ⠀⠀⠹⣿⣿⣿⣿⣿⣾⠛⠉⣿⣦⣸⣿⣿⣿⣿⣿⣿⣿⣿⣿⡗⣰⣿⣿⣿⠀⠀⣿⠀⠀⠀⠀⠀⠀⣀⡀⠀⠀
                    // ⠀⠀⡰⠋⠉⠉⠉⣿⠉⠀⠀⠉⢹⡿⠋⠉⠉⠉⠛⢿⣿⠉⠉⠋⠉⠉⠻⣿⠀⠀⣿⠞⠉⢉⣿⠚⠉⠉⠉⣿⠀
                    // ⠀⠀⢧⠀⠈⠛⠿⣟⢻⠀⠀⣿⣿⠁⠀⣾⣿⣧⠀⠘⣿⠀⠀⣾⣿⠀⠀⣿⠀⠀⠋⠀⢰⣿⣿⡀⠀⠛⠻⣟⠀
                    // ⠀⠀⡞⠿⠶⠄⠀⢸⢸⠀⠀⠿⢿⡄⠀⠻⠿⠇⠀⣸⣿⠀⠀⣿⣿⠀⠀⣿⠀⠀⣶⡀⠈⢻⣿⠿⠶⠆⠀⢸⡇
                    // ⠀⠀⠧⢤⣤⣤⠴⠋⠈⠦⣤⣤⠼⠙⠦⢤⣤⡤⠶⠋⠹⠤⠤⠿⠿⠤⠤⠿⠤⠤⠿⠳⠤⠤⠽⢤⣤⣤⠴⠟⠀
                    // """);
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
            letterMembership = BINGO.charAt((randomNumber - (randomNumber % 16)) / 15);

            for (int i = 0; i < getRandomNumber(50, 76); i++) {
                System.out.print("\rSa letra sang... ");
                System.out.print(BINGO.charAt(getRandomNumber(0, BINGO.length())));
                Thread.sleep(25);
            }

            System.out.print("\b" + letterMembership + "!\n");

            for (int i = 0; i < getRandomNumber(40, 61); i++) {
                System.out.print(getRandomNumber(1, BINGO_MAX + 1) + "\r");
                Thread.sleep(25 + i);
            }

            System.out.println(letterMembership + " " + randomNumber + "!\n");
            numberIsInCardSaysUser = isYesWhenPrompted("Do you have " + randomNumber + " in any of your carda?");

            System.out.println();

            if (USER_CARDS_REPR.indexOf(randomNumberRepr) != -1) {
                System.out.print("May ara ka sang " + randomNumber + ". ");
                if (numberIsInCardSaysUser) {
                    USER_MARKED_NUM_REPR += randomNumberRepr;
                    System.out.print("Markahan ang imo card...");
                } else {
                    System.out.println("Nugon, hindi pag markahan.");
                }
            } else {
                System.out.print("Wala ka " + randomNumber + ". ");
                do {
                    if (numberIsInCardSaysUser) {
                        System.out.printf("Warning! %d chances left!", chance  );
                        if(chance == 0){
                        USER_MONEY -= DEDUCTION;
                        System.out.println("Buhinan imo kwarta P" + DEDUCTION + ".");
                        System.out.println("Remaining balance: P" + USER_MONEY);
                        chance += 3;
                    } 
                       
                      --chance;   
                    }
                } while( chance == 3);
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
        int patternBits;
        int currentNum;
        int cardCount;
        char currentNumRepr;
        String card_repr;
        boolean isMarked, isMiddle;
        boolean isPlayer = player.equals(USERNAME);

        if (isPlayer) {
            USER_CARD_PATTERNS_REPR = "";
            card_repr = USER_CARDS_REPR;
            cardCount = USER_CARD_COUNT;
        } else {
            COMP_CARD_PATTERNS_REPR = "";
            card_repr = COMP_CARDS_REPR;
            cardCount = COMP_CARD_COUNT;
        }

        // Use conditionals to further control the printing of the card.
        for (int i = 0; i < cardCount; i++) {
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

        // This is what the indices would look like:
        // * 24 23 22 21 20
        // * 19 18 17 16 15
        // * 14 13 12 11 10
        // * 09 08 07 06 05
        // * 04 03 02 01 00

        char currChar;
        int bits = 0;
        int currentSelection = MSB_INDEX;
        boolean isInTool = true;

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

            System.out.println("\nB" + GRID_SEP + "I" + GRID_SEP + "N" + GRID_SEP + "G" + GRID_SEP + "O\n");

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

            System.out.println("\n'*' : MARKED\t   '-' : UNMARKED\n\n");
            System.out.println("[wasd]\tMove selection");
            System.out.println("[" + markUnmarkSqr + "]\tFlip selection");
            System.out.println("[" + markUnmarkRow + "]\tFlip whole row");
            System.out.println("[" + markUnmarkCol + "]\tFlip whole column");
            System.out.println("[" + useCustomPatt + "]\tAdd as winning pattern");
            System.out.println("[" + resetPattern + "]\tReset pattern");
            System.out.println("[" + exitPattTool + "]\tExit tool");

            System.out.print("\nAction: ");

            // @formatter:off
            switch (SCANNER.nextLine().toLowerCase().strip()) {
                case markUnmarkSqr -> bits ^= 1 << currentSelection;
                case "w" -> {if (currentSelection < 20) currentSelection += 5;}
                case "a" -> {if (currentSelection % 5 < 4) currentSelection += 1;}
                case "s" -> {if (currentSelection > 4) currentSelection -= 5;}
                case "d" -> {if (currentSelection % 5 > 0) currentSelection -= 1;}
                case markUnmarkRow -> bits ^= 0b11111 << currentSelection - (currentSelection % 5);
                // 0x 108421 = 0b 00001 00001 00001 00001 00001
                case markUnmarkCol -> bits ^= 0x108421 << currentSelection % 5;

                case useCustomPatt -> {
                    if (bits == 0) {
                        printInteractive("Please mark something in the pattern.");
                    } else if (DEFAULT_WINNING_PATTERNS_REPR.contains(bits + "") || CUSTOM_PATTERNS_REPR.contains(bits + "")) {
                        printInteractive("The current pattern is either a default pattern or alredy in use.");
                    } else {
                        System.out.print("What would you like to name the pattern?: ");
                        String customPatternName = SCANNER.nextLine();
                        CUSTOM_PATTERNS_REPR += bits + SEPARATOR_STRING;
                        CUSTOM_PATTERN_NAMES += "\"" + customPatternName + "\", ";
                        printInteractive("Added \"" + customPatternName + "\" as a winning pattern.");

                        if (isYesWhenPrompted("Reset the grid?")) {
                            bits = 0;
                            currentSelection = MSB_INDEX;
                        }
                    }
                }

                case resetPattern -> {
                    if (isYesWhenPrompted("Are you sure you want to reset?")) {
                        bits = 0;
                        currentSelection = MSB_INDEX;
                    }
                }

                case exitPattTool -> {
                    if (bits > 0) {
                        isInTool = !isYesWhenPrompted("Are you sure you want to discard changes and exit?");
                    } else {
                        isInTool = !isYesWhenPrompted("Are you sure you want to exit?");
                    }
                }

                default -> printInteractive("Invalid input.");
            }
            // @formatter:on
        } while (isInTool);
    }

    static int cardContainsWinningPattern(String player) {
        int cardCount;
        int winningPatternBits, cardPatternBits;
        int nextCardSep, nextWinSep;
        int nextCardPatternIndex, nextWinningPatternIndex = 0;
        String cardPatternsRepr;
        String winningPatternRepr, cardPatternRepr;

        if (player.equals(COMP_NAME)) {
            cardPatternsRepr = COMP_CARD_PATTERNS_REPR;
            cardCount = COMP_CARD_COUNT;
        } else {
            cardPatternsRepr = USER_CARD_PATTERNS_REPR;
            cardCount = USER_CARD_COUNT;
        }

        for (int patternCount = 0; patternCount < WINNING_PATTERN_COUNT; patternCount++) {
            nextWinSep = WINNING_PATTERNS_REPR.substring(nextWinningPatternIndex).indexOf(SEPARATOR_CHAR)
                    + nextWinningPatternIndex;

            winningPatternRepr = WINNING_PATTERNS_REPR.substring(nextWinningPatternIndex, nextWinSep);
            winningPatternBits = Integer.parseInt(winningPatternRepr);

            nextWinningPatternIndex += winningPatternRepr.length() + 1;

            nextCardPatternIndex = 0;

            for (int count = 0; count < cardCount; count++) {
                nextCardSep = cardPatternsRepr.substring(nextCardPatternIndex).indexOf(SEPARATOR_CHAR)
                        + nextCardPatternIndex;

                cardPatternRepr = cardPatternsRepr.substring(nextCardPatternIndex, nextCardSep);
                cardPatternBits = Integer.parseInt(cardPatternRepr);

                nextCardPatternIndex += cardPatternRepr.length() + 1;

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

    static void randomizeDefaultWinningPattern() {
        // @formatter:off
        switch (RANDOM.nextInt(4)) {
            case (0) -> {
                System.out.println("DEFAULT PATTERN: CROSSWISE!");
                WINNING_PATTERNS_REPR = convertPattToInt("*---*-*-*---*---*-*-*---*") + SEPARATOR_STRING;
            }
            case (1) -> {
                System.out.println("DEFAULT PATTERN: BLACKOUT!");
                WINNING_PATTERNS_REPR = convertPattToInt("*************************") + SEPARATOR_STRING;
            }
            case (2) -> {
                System.out.println("DEFAULT PATTERN: VERTICAL 5s!");
                WINNING_PATTERNS_REPR = (
                          convertPattToInt("*----*----*----*----*----") + SEPARATOR_STRING
                        + convertPattToInt("-*----*----*----*----*---") + SEPARATOR_STRING
                        + convertPattToInt("--*----*----*----*----*--") + SEPARATOR_STRING
                        + convertPattToInt("---*----*----*----*----*-") + SEPARATOR_STRING
                        + convertPattToInt("----*----*----*----*----*") + SEPARATOR_STRING
                );
            }
            case (3) -> {
                System.out.println("DEFAULT PATTERN: HORIZONTAL 5s!");
                WINNING_PATTERNS_REPR = (
                          convertPattToInt("*****--------------------") + SEPARATOR_STRING
                        + convertPattToInt("-----*****---------------") + SEPARATOR_STRING
                        + convertPattToInt("----------*****----------") + SEPARATOR_STRING
                        + convertPattToInt("---------------*****-----") + SEPARATOR_STRING
                        + convertPattToInt("--------------------*****") + SEPARATOR_STRING
                );
            }
        }
        // @formatter:on
    }

    static void playTutorial() throws IOException, InterruptedException {
        cls();
        printInteractive("Hello! Welcome to the BINGO tutorial!");

        // Limit the output to about 50 characters.
        tutorial: while (true) {
            cls();
            System.out.println("BINGO TUTORIAL\n");
            System.out.println("""
                    What do you want to know?
                    1. Base Mechanics
                    2. Money and Buying Cards
                    3. Creating Patterns

                    4. Start Game!
                    """);
            System.out.print("Enter your choice: ");

            // @formatter:off
            switch (SCANNER.nextLine()) {

                case ("1") -> {
                    cls();
                    System.out.println("BASE MECHANICS\n");
                    printInteractive("BINGO is a game of chance.");
                    printInteractive("Once every turn, a number is rolled randomly.");
                    printInteractive("""
                            The middle, denoted FS, means free square.
                            Consider it already marked.""");
                    System.out.println();
                    cls();

                    printInteractive("""
                            After a roll,
                            on a BINGO card that looks like this:

                            B\tI\tN\tG\tO
                            12\t22\t43\t51\t72
                            5\t23\t35\t57\t61
                            9\t28\tFS\t48\t69
                            11\t29\t41\t49\t62
                            6\t19\t42\t50\t65

                            You are prompted if the number is in one of your cards.""");
                    printInteractive("\n> IF you say yes and it is, the square gets marked.");
                    printInteractive("> If you say yes but it isn't, your money gets deducted.");
                    printInteractive("> If you say no but it is, it will not get marked.");
                    printInteractive("> IF you say no and it isn't, nothing will happen.");

                    cls();
                    printInteractive("""
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

                    printInteractive("69!");

                    if (isYesWhenPrompted("Is 69 in the card?")) {
                        printInteractive("""

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
                        printInteractive("""

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
                    printInteractive("The game has two types of patterns:");
                    printInteractive("""

                            1. CUSTOMIZED PATTERNS
                            - The host is prompted whether to create custom patterns.
                            - If the host does not wish so, the game uses back to default patterns.
                            - The game is won if one of the host's customized patterns is achieved.
                            """);
                    printInteractive("""

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
                    printInteractive("Explore more of the tutorial or go straight to the game!");
                }

                case "2" -> {
                    cls();
                    System.out.println("MONEY AND BUYING CARDS\n");
                    System.out.println("You enter with P" + STARTING_MONEY);
                    printInteractive("But you start the game with one free card.");
                    printInteractive("Once the first game ends, you can buy cards.");

                    cls();
                    printInteractive("\n1 card = P" + CARD_COST);
                    printInteractive("\nAfter a game, the winner gets P" + PRIZE_PER_WIN);
                    printInteractive("""
                            You can buy new cards as much as you want
                            as long as you can afford it.""");
                    printInteractive("""
                            If you or the computer reaches zero money,
                            then the game is over.""");
                    printInteractive("That's all about money system and buying cards!");
                }

                case "3" -> {
                    cls();
                    System.out.println("CREATING PATTERNS\n");
                    printInteractive("Welcome, Host!");
                    printInteractive("You can create patterns with the Pattern Maker Tool.");
                    printInteractive("""
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
                    printInteractive("\nFirst, select which square you want to mark as part of the winning pattern.\n");
                    printInteractive("To move the selection:");
                    printInteractive("'w' - moves upward.");
                    printInteractive("'s' - moves downward.");
                    printInteractive("'a' - moves to the left.");
                    printInteractive("'d' - moves to the right.");

                    printInteractive("\nThere are several ways we can modify the pattern:");
                    printInteractive("'" + markUnmarkSqr + "' - mark the current square;");
                    printInteractive("'" + markUnmarkCol + "' - mark the whole column where the selection lies;");
                    printInteractive("'" + markUnmarkRow + "' - mark the whole row where the selections lies;");
                    printInteractive("'" + resetPattern + "' - reset all the customized pattern;");
                    printInteractive("'" + exitPattTool + "' - if you're done or you don't want to make patterns.");
                }
                case "4" -> {
                    cls();
                    printInteractive("You're all settled! Good luck and have fun!");
                    break tutorial;
                }
                default -> printInteractive("\nInvalid input!");
            }
            // @formatter:on
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
        return RANDOM.nextInt(max - min) + min;
    }

    static char getNumberRepr(int number) {
        return (char) (number + ASCII_MIN);
    }

    static int getReprNumber(char repr) {
        return repr - ASCII_MIN;
    }

    static void printInteractive(String s) {
        System.out.print(s + " (ENTER) ");
        SCANNER.nextLine();
    }

    static void cls() throws IOException, InterruptedException {
        CMD_PROCESS.start().waitFor();
    }
}
