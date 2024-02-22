import java.io.IOException;
import java.util.Scanner;
import java.util.Random;

public class BINGO {
    // GLOBAL VARIABLES
    static Random RAND = new Random();
    static Scanner SCANNER = new Scanner(System.in);

    // " " (32 in ASCII) AS SEPARATOR FOR PATTERN AND  AND "!" AS FREE SPACE IN THE MIDDLE
    static final char SEPARATOR = 32, FREE_SPACE = SEPARATOR + 1;
    static final int ASCIIMIN = FREE_SPACE + 1, BINGOMAX = 75, LENGTH = 25, MIDDLE = 12;

    static final String TEMPLATE_PATTERN = "------------*------------";
    static String WINNING_PATTERNS_SEQUENCES = "";
    static String WINNING_PATTERNS_INTS = "";
    static String ROLLED_NUMBERS_REPR = "";
    static String CARDS_REPR = "";
    static String CARD_PATTERNS_SEQUENCES = "";
    static String CARD_PATTERNS_INTS = "";
    static String GRID_SEP = "\t";

    static int PATTERN_COUNT = 1, CARD_COUNT;

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
        System.out.print("How many cards? ");
        CARD_COUNT = SCANNER.nextInt();
        createBingoCardRepr(CARD_COUNT);
        SCANNER.nextLine();

        // PATTERN CREATION
        String response;
        do {
            System.out.print("Do you want to create a custom winning pattern? (y/n): ");
            response = SCANNER.nextLine().strip().toLowerCase();
        } while (!(response != "y" || response != "n"));

        if (response.equals("n")) {
            WINNING_PATTERNS_SEQUENCES = "*---*-*-*---*---*-*-*---*";
        }

        while (response == "y") {
            patternCreation();
            PATTERN_COUNT++;
            do {
                System.out.print("Create another pattern? (y/n): ");
                response = SCANNER.nextLine().strip().toLowerCase();
            }  while (!(response != "y" || response != "n"));
        }

        // HELP MODULE
        // playTutorial();
        convertWinningPatternToInts();

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
         * The SPACE is reserved as the separator between player cards.
         */
        int min, max;
        char randNumberRepr;
        String card;
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
        * A `pattern` is a string composed of '*'s and '-', where '*' is a marked square, while '-' is the default.
        * All winning patterns, are stored in a single `bingoWinningPatterns` String, separated by  ',' between.
        *
        * Player bingo cards are also converted into patterns, and we can simply iterate the cardPattern against the winningPatterns if the marks are matching.
        */

        // TOOL TUTORIAL
        patternCreationTutorial();

        String action;
        boolean inTool = true;
        int currentSelection = 0;
        String pattern = TEMPLATE_PATTERN;

        while (inTool) {
            cls();
            // PRINTING THE CURRENT PATTERN MAKER CARD
            System.out.println("PATTERN MAKER TOOL\n");
            System.out.println("B" + GRID_SEP + "I" + GRID_SEP + "N" + GRID_SEP + "G" + GRID_SEP + "O");
            for (int j = 0; j < LENGTH; j++) {
                if (j == currentSelection) {
                    System.out.print("[" + pattern.charAt(j) + "]");
                } else {
                    System.out.print(pattern.charAt(j));
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
                    pattern = replace(pattern, currentSelection, "*");
                } else if (action.equals("z")) {
                    int leftMostIndex = 0;
                    if (currentSelection <= 4) {
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
                    pattern = pattern.substring(0, leftMostIndex) + '*'+'*'+'*'+'*'+'*' + pattern.substring(leftMostIndex+5);
                } else if (action.equals("x")) {
                    String temporaryString = "";
                    for (int i = 0; i < LENGTH; i++) {
                        if (i % 5 == currentSelection % 5) {
                            temporaryString += '*';
                        } else {
                            temporaryString += pattern.charAt(i);
                        }
                    }
                    pattern = temporaryString;
                } else if (action.equals("e")) {
                    System.out.println("Exiting pattern tool...");
                    inTool = false;
                } else if (action.equals("r")) {
                    pattern = TEMPLATE_PATTERN;
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
        WINNING_PATTERNS_SEQUENCES += pattern;
        PATTERN_COUNT++;
    }

    static void updateCardPatterns() {
        CARD_PATTERNS_SEQUENCES = "";
        char currentChar;
        for (int i = 0; i < CARD_COUNT; i++) {
            for (int j = 0; j < LENGTH; j++) {
                currentChar = CARDS_REPR.charAt(j);
                if (currentChar == FREE_SPACE) CARD_PATTERNS_SEQUENCES += '*';
                CARD_PATTERNS_SEQUENCES += (ROLLED_NUMBERS_REPR.contains(currentChar+"")) ? '*' : '-';
            }
        }

        String pattern;
        for (int k = 0; k < CARD_COUNT; k++) {
            pattern = CARD_PATTERNS_SEQUENCES.substring(k * LENGTH, k * LENGTH + LENGTH);
            CARD_PATTERNS_INTS += convertPattToInt(pattern) + "" + SEPARATOR;
        }
    }

    static void convertWinningPatternToInts() {
        String pattern;
        for (int i = 0; i < PATTERN_COUNT; i++) {
            pattern = WINNING_PATTERNS_SEQUENCES.substring(i * LENGTH, i * LENGTH + LENGTH);
            WINNING_PATTERNS_INTS += convertPattToInt(pattern) + "" + SEPARATOR;
        }
    }

    static boolean cardContainsWinningPattern() {
        // for the sake of my sanity, please afford us the use of parseToInt...
        boolean isWon = true;
        int winningPatternBits, cardPatternBits;
        for (String patternInts : WINNING_PATTERNS_INTS.split(SEPARATOR+"")) {
            winningPatternBits = Integer.parseInt(patternInts);
            for (String cardPatternInts : CARD_PATTERNS_INTS.split(SEPARATOR+"")) {
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
