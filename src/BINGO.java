import java.io.IOException;
import java.util.Scanner;
import java.util.Random;

public class BINGO {
    // GLOBAL VARIABLES
    static Random RAND = new Random();
    static Scanner SCANNER = new Scanner(System.in);

    // " " (32 in ASCII) AS SEPARATOR FOR PATTERN AND  AND "!" AS FREE SPACE IN THE MIDDLE
    static final char SEPCHAR = 32, FREE_SPACE = SEPCHAR + 1;
    static final String SEPSTR = SEPCHAR + "";
    static final int ASCIIMIN = FREE_SPACE + 1, BINGOMAX = 75, LENGTH = 25, MIDDLE = 12;

    /*
     * There is NO SEPARATOR between REPRS and SEQUENCES, only in INTS,
     * since we can control the printing of card repr, and there is no need to print sequences (for now).
     * Substring of each reprs and sequences is [start, start + LENGTH], where start = i * LENGTH.
     * However, if you decided to add separation, use start = i * LENGTH + (i * 1).
     */

    static String ROLLED_NUMBERS_REPR;
    static String WINNING_PATTERNS_REPR;
    static String CARDS_REPR;
    static String CARD_PATTERNS_REPR;
    static String GRID_SEP = "\t";

    static int PATTERN_COUNT = 0, CARD_COUNT;

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
        CARDS_REPR = "";
        WINNING_PATTERNS_REPR = "";
        ROLLED_NUMBERS_REPR = "";

        System.out.print("How many cards? ");
        CARD_COUNT = SCANNER.nextInt();
        createBingoCardRepr(CARD_COUNT);
        SCANNER.nextLine();

        // TODO change the loopings here.
        // PATTERN CREATION
        String response;
        do {
            System.out.print("Do you want to create a custom winning pattern? (y/n): ");
            response = SCANNER.nextLine().strip().toLowerCase();
        } while (!(response != "y" || response != "n"));

        if (response.equals("n")) {
            WINNING_PATTERNS_REPR += convertPattToInt("*---*-*-*---*---*-*-*---*");
            PATTERN_COUNT++;
        }

        // TOOL TUTORIAL
        // patternCreationTutorial();

        while (response.equals("y")) {
            patternCreation();
            do {
                System.out.print("Create another pattern? (y/n): ");
                response = SCANNER.nextLine().strip().toLowerCase();
            }  while (!(response != "y" || response != "n"));
        }

        // HELP MODULE
        // playTutorial();

        //convertWinningPatternToInts();

        // MAIN GAME LOOP
        letsPlayBingo();
    }

    static void letsPlayBingo() throws IOException, InterruptedException {
        char letter;
        char randomNumberRepr;
        int randomNumber;
        String membership;

        cls();
        if (PATTERN_COUNT == 1) System.out.println("USING WINNING PATTERN: X\n");
        boolean isPlaying = true;

        while (isPlaying) {
            do {
                randomNumberRepr = getNumberRepr(getRandomNumber(1, BINGOMAX + 1));
            } while (ROLLED_NUMBERS_REPR.contains(randomNumberRepr+""));

            randomNumber = getReprNumber(randomNumberRepr);
            membership = (CARDS_REPR.contains(randomNumberRepr+"")) ? "May " : "Wala ";

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

            updateCardPatterns();

            if (cardContainsWinningPattern()) {
                System.out.println("BINGO!!!");
                printBingoCardsRepr();
                isPlaying = false;
                break;
            };

            // System.out.print(BingoShake);
            printBingoCardsRepr();

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

    static void createBingoCardRepr(int cardCount) {
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
                    } while (card.contains(randNumberRepr+""));
                    card += randNumberRepr+"";
                }
            }
            CARDS_REPR += card;
        }
    }

    static void printBingoCardsRepr() {
        /*
        * Since bingoCardRepr is a 1-dimentional form, there is no need for nested loops.
        * I instead used conditionals to further control the printing of the card.
        */
        char currentNumRepr;
        int currentNum;

        for (int i = 0; i < CARD_COUNT; i++) {
            System.out.println("CARD NO. " + (i + 1));
            System.out.println("B" + GRID_SEP + "I" + GRID_SEP + "N" + GRID_SEP + "G" + GRID_SEP + "O");
            for (int j = i * LENGTH; j < i * LENGTH + LENGTH; j++) {
            currentNumRepr = CARDS_REPR.charAt(j);

                if (currentNumRepr == FREE_SPACE) {
                    System.out.print("FS" + GRID_SEP);
                } else {
                    currentNum = getReprNumber(currentNumRepr);
                    // enclose the number with parentheses if the number is already called out, else print as it is
                    System.out.print(ROLLED_NUMBERS_REPR.contains(currentNumRepr+"") ? "(" + currentNum + ")" : currentNum);
                    System.out.print(GRID_SEP);
                }
                if (j % 5 == 4) {
                    System.out.print('\n');
                }
            }
            System.out.println();
        }
    }

    static void patternCreation() throws IOException, InterruptedException {
        /*
        * A `pattern` is composed of marked squares in a player card and a winning pattern.
        * It is (Repr)esented/stored as a String of the number equivalent to that pattern's squares when converted into a series of bits,
        * where a marked square is 1 and an unmarked one as 0 (this number is an int, 2^32 bit, enough space for a 25-bit max value).
        *
        * Checking for a matching pattern in the card involves converting that String number into an int and perform bitwise AND
        * (see the pattern checking function for actual implementation).
        */


        int patternBits = 0;
        char currChar;
        int currentSelection = 0;
        boolean inTool = true;

        String action;
        int leftMost, topMost;

        while (inTool) {
            cls();
            // PRINTING THE CURRENT PATTERN MAKER CARD
            System.out.println("PATTERN MAKER TOOL\n");
            System.out.println("B" + GRID_SEP + "I" + GRID_SEP + "N" + GRID_SEP + "G" + GRID_SEP + "O");
            for (int i = 0; i < LENGTH; i++) {
                currChar = ((patternBits & (1 << (LENGTH - 1 - i))) != 0) ? '*' : '-';
                if (i == currentSelection) {
                    System.out.print("[" + currChar + "]");
                } else if (i == MIDDLE) {
                    System.out.print('*');
                } else {
                    System.out.print(currChar);
                }

                if (i % 5 == 4) {
                    System.out.println('\n');
                } else {
                    System.out.print(GRID_SEP);
                }
            }
            System.out.println();

            while (true) {
                // TODO add demarking of selection
                System.out.println("[wasd] Move current selection");
                System.out.println("[q] Mark current selection");
                System.out.println("[z] Mark whole row");
                System.out.println("[x] Mark whole column");
                System.out.println("[e] Save and exit");
                // System.out.println("[n] Discard and exit");
                System.out.println("[r] Reset");
                System.out.print("Action: ");
                action = SCANNER.nextLine().toLowerCase().strip();

                /*
                 * The manipulation of bits is reversed in relation to the current selection
                 * (you can see subtractions from LENGTH) because the pattern starts from the leftmost bit (most significant)
                 * going to the right, and the currentSelection starts from 0 (least significant).
                 * e.g. 00000 and currentSelection is 0. The 0th shift is actually the last bit, and the shift by LENGTH-1 amount the opposite.
                 */
                if (action.equals("q")) {
                    // mark current selection
                    patternBits = patternBits | 1 << LENGTH - currentSelection - 1;
                } else if (action.equals("z")) {
                    // mark whole row
                    // currentSelection - (currentSelection % 5) calculates the leftmost index of currentSelection's row
                    leftMost = currentSelection - (currentSelection % 5);
                    for (int i = 0; i < 5; i++) {
                        patternBits = patternBits | 1 << LENGTH - leftMost - i - 1;
                    }
                } else if (action.equals("x")) {
                    // mark whole column
                    topMost = currentSelection % 5;
                    for (int i = 0; i < 5; i++) {
                        System.out.println(LENGTH - topMost - (i * 5) - 1);
                        patternBits = patternBits | 1 << LENGTH - topMost - (i * 5) - 1;
                    }
                } else if (action.equals("e")) {
                    inTool = false;
                } else if (action.equals("r")) {
                    patternBits = 0;
                    currentSelection = 0;
                } else if (action.equals("w") && currentSelection > 4) {
                    currentSelection -= 5;
                } else if (action.equals("a") && currentSelection % 5 > 0) {
                    currentSelection -= 1;
                } else if (action.equals("s") && currentSelection < 20) {
                    currentSelection += 5;
                } else if (action.equals("d") && currentSelection % 5 < 4) {
                    currentSelection += 1;
                } else {
                    System.out.println("TODO");
                }
                break;
            }
        }
        WINNING_PATTERNS_REPR += patternBits;
        PATTERN_COUNT++;
    }

    static void updateCardPatterns() {
        CARD_PATTERNS_REPR = "";
        int bits = 0;
        for (int i = 0; i < CARD_COUNT; i++) {
            for (int j = 0; j < LENGTH; j++) {
                if ((bits & (1 << (LENGTH - 1 - i))) == 0 || j != MIDDLE + (LENGTH * i));
                bits = bits | 1 << (LENGTH - 1);
            }
            CARD_PATTERNS_REPR += bits + SEPSTR;
        }
    }

    static boolean cardContainsWinningPattern() {
        // for the sake of my sanity, please afford us the use of parseToInt and enhanced for loops...
        boolean isWon = true;
        int winningPatternBits, cardPatternBits;
        for (String patternInts : WINNING_PATTERNS_REPR.split(SEPSTR)) {
            winningPatternBits = Integer.parseInt(patternInts);
            for (String cardPatternInts : CARD_PATTERNS_REPR.split(SEPSTR)) {
                cardPatternBits = Integer.parseInt(cardPatternInts);
                if ((winningPatternBits & cardPatternBits) != winningPatternBits) isWon = false;
            }
            if (isWon) return isWon;
        }
        return isWon;
    }

	static int convertPattToInt(String pattern) {
		int bytes = 0;
        int len = pattern.length();
		for (int i = 0; i < len; i++) {
			if (pattern.charAt(i) == '-') continue;
			bytes = bytes | (1 << len-i);
		}
		return bytes;
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
